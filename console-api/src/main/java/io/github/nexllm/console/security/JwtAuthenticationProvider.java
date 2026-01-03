package io.github.nexllm.console.security;

import io.github.nexllm.console.auth.config.AuthProperties;
import io.github.nexllm.security.auth.ConsoleAuthenticationToken;
import io.github.nexllm.security.jwt.JwtClaims;
import io.github.nexllm.security.jwt.JwtUtils;
import io.github.nexllm.security.jwt.VerifyOnlyKeyProvider;
import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationProvider(AuthProperties authProperties) throws Exception {
        this.jwtUtils = new JwtUtils(new VerifyOnlyKeyProvider(authProperties.jwt().publicKey()));
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        ConsoleAuthenticationToken jwtAuthenticationToken = (ConsoleAuthenticationToken) authentication;
        try {
            JwtClaims claims = jwtUtils.verify(jwtAuthenticationToken.getCredentials().toString());
            ConsoleUserPrincipal principal = ConsoleUserPrincipal.builder()
                .userId(claims.userId())
                .tenantId(claims.tenantId())
                .username(claims.username())
                .roles(claims.roles())
                .build();
            return new ConsoleAuthenticationToken(principal);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT Token", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ConsoleAuthenticationToken.class.isAssignableFrom(authentication);
    }
}