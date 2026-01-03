package io.github.nexllm.gateway.config;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.exception.LLMError;
import io.github.nexllm.common.exception.LLMError.ErrorDetails;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.infra.redis.accessor.ReactiveRedisAccessor;
import io.github.nexllm.infra.redis.key.RedisKeys;
import io.github.nexllm.security.SecurityConstants;
import io.github.nexllm.security.auth.ApiAuthenticationToken;
import io.github.nexllm.security.auth.PlaygroundAuthenticationToken;
import io.github.nexllm.security.jwt.JwtClaims;
import io.github.nexllm.security.jwt.JwtUtils;
import io.github.nexllm.security.jwt.PrincipalSource;
import io.github.nexllm.security.jwt.VerifyOnlyKeyProvider;
import io.github.nexllm.security.principal.ApiUserPrincipal;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final ReactiveRedisAccessor reactiveRedisAccessor;
    private final JwtUtils jwtUtils;
    private final String[] permitAllPaths = {
        "/v1/auth/login",
        "/webjars/**",
        "/swagger**/**",
        "/v3/**",
    };

    public SecurityConfig(ReactiveRedisAccessor reactiveRedisAccessor, AuthProperties authProperties) throws Exception {
        this.reactiveRedisAccessor = reactiveRedisAccessor;
        this.jwtUtils = new JwtUtils(new VerifyOnlyKeyProvider(authProperties.jwt().publicKey()));
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(CsrfSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(it -> it.pathMatchers(permitAllPaths).permitAll().anyExchange().authenticated())
            .addFilterAt(authenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    private Mono<Void> unauthorizedResponse(WebFilterExchange exchange, String message) {
        return writeJsonResponse(exchange, message);
    }

    private Mono<Void> writeJsonResponse(WebFilterExchange exchange, String message) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        LLMError llmError = new LLMError(ErrorDetails.builder()
            .code(ErrorCode.AU_UNAUTHORIZED.name())
            .message(message)
            .build());
        byte[] bytes = JsonUtils.toJson(llmError).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private AuthenticationWebFilter authenticationFilter() {
        ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.pathMatchers(permitAllPaths);
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setRequiresAuthenticationMatcher(new NegatedServerWebExchangeMatcher(matcher));
        filter.setServerAuthenticationConverter(authenticationConverter());
        filter.setAuthenticationFailureHandler((exchange, ex) -> unauthorizedResponse(exchange, ex.getMessage()));
        return filter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            if (authentication instanceof ApiAuthenticationToken openApiAuthenticationToken) {
                String apiKey = openApiAuthenticationToken.getCredentials().toString();
                return reactiveRedisAccessor.get(RedisKeys.API_KEY, apiKey)
                    .flatMap(meta -> {
                        if (meta == null) {
                            return Mono.error(new BadCredentialsException("Invalid api key"));
                        }
                        return Mono.just(ApiUserPrincipal.builder()
                            .source(PrincipalSource.API)
                            .tenantId(meta.tenantId())
                            .userId(meta.userId())
                            .apiKeyId(meta.apiKeyId())
                            .build());
                    })
                    .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid api key")))
                    .map(it -> new ApiAuthenticationToken(it));
            }
            PlaygroundAuthenticationToken token = (PlaygroundAuthenticationToken) authentication;
            JwtClaims principal = jwtUtils.verify(token.getCredentials().toString());
            PlaygroundAuthenticationToken authenticationToken = new PlaygroundAuthenticationToken(
                ApiUserPrincipal.builder()
                    .userId(principal.userId())
                    .tenantId(principal.tenantId())
                    .apiKeyId(principal.apiKeyId())
                    .source(principal.source())
                    .build());
            return Mono.just(authenticationToken);
        };
    }

    @Bean
    public ServerAuthenticationConverter authenticationConverter() {
        return this::extractAuthorizationToken;
    }

    private Mono<Authentication> extractAuthorizationToken(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(apiKey)) {
            log.info("Authorization header is empty");
            return Mono.error(new BadCredentialsException("Unauthorized"));
        }
        if (apiKey.startsWith(SecurityConstants.Jwt_PREFIX)) {
            String authKey = apiKey.substring(SecurityConstants.Jwt_PREFIX.length()).trim();
            return Mono.just(new PlaygroundAuthenticationToken(authKey));
        }
        if (apiKey.startsWith(SecurityConstants.BEARER_PREFIX)) {
            String authKey = apiKey.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
            return Mono.just(new ApiAuthenticationToken(authKey));
        }
        return Mono.error(new BadCredentialsException("Invalid Bearer token"));
    }
}
