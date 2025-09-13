package io.github.nexllm.auth.security;

import io.github.nexllm.auth.config.AuthProperties;
import io.github.nexllm.auth.util.JwtUtils;
import io.github.nexllm.security.model.AuthUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationProvider(AuthProperties authProperties) {
        this.jwtUtils = new JwtUtils(authProperties.jwt().publicKey(), authProperties.jwt().privateKey());
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        try {
            AuthUser user = jwtUtils.verifyAndParse(jwtAuthenticationToken.getAccessToken());
            return new JwtAuthenticationToken(user);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT Token", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}