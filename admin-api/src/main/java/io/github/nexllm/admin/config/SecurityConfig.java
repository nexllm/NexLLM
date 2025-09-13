package io.github.nexllm.admin.config;

import static io.github.nexllm.common.constants.NexLLMConstants.ERROR_URL;

import io.github.nexllm.admin.security.JwtAuthenticationProvider;
import io.github.nexllm.admin.security.JwtAuthenticationToken;
import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.security.SecurityConstants;
import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final String[] permitAllPaths = {
        "/api/v1/auth/**",
        "/api/v1/.well-known/**",
        "/webjars/**",
        "/swagger-ui.html",
        "/swagger**/**",
        "/v3/**",
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllPaths).permitAll()
                .anyRequest()
                .authenticated()
            )
            .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private AuthenticationFilter jwtAuthenticationFilter() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(),
            jwtAuthenticationConverter());
        authenticationFilter.setRequestMatcher(request -> {
            for (String permitAllPath : permitAllPaths) {
                if (antPathMatcher.match(permitAllPath, request.getRequestURI())) {
                    return false;
                }
            }
            return true;
        });
        authenticationFilter.setFailureHandler((request, response, exception) -> {
            ErrorCode errorCode = ErrorCode.AU_INVALID_TOKEN;
            ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
            problemDetail.setTitle("Invalid credentials");
            problemDetail.setDetail(exception.getMessage());
            problemDetail.setInstance(URI.create(request.getRequestURI()));
            problemDetail.setType(URI.create(ERROR_URL));
            problemDetail.setProperty("error_code", errorCode.getCode());
            response.setStatus(errorCode.getHttpStatus().value());
            response.setContentType("application/json");
            response.getWriter().write(JsonUtils.toJsonWithoutNull(problemDetail));
        });
        authenticationFilter.setSuccessHandler((request, response, authentication) -> {

        });
        return authenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    public AuthenticationConverter jwtAuthenticationConverter() {
        return request -> {
            String jwtToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
            if (!StringUtils.hasText(jwtToken)) {
                String accessToken = Arrays.stream(request.getCookies())
                    .filter(it -> it.getName().equals(SecurityConstants.ACCESS_TOKEN))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
                String refreshToken = Arrays.stream(request.getCookies())
                    .filter(it -> it.getName().equals(SecurityConstants.REFRESH_TOKEN))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
                if (StringUtils.hasText(accessToken) || StringUtils.hasText(refreshToken)) {
                    return new JwtAuthenticationToken(accessToken, refreshToken);
                }
                throw new BadCredentialsException("Invalid access token");
            }
            if (!jwtToken.startsWith("Bearer ")) {
                throw new BadCredentialsException("Invalid access token format, expected 'Bearer <access_token>'");
            }
            return new JwtAuthenticationToken(jwtToken.substring(SecurityConstants.BEARER_PREFIX.length()));
        };
    }
}
