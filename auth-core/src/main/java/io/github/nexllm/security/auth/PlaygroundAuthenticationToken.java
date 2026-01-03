package io.github.nexllm.security.auth;

import io.github.nexllm.security.principal.ApiUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PlaygroundAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;
    private final ApiUserPrincipal principal;

    public PlaygroundAuthenticationToken(String credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public PlaygroundAuthenticationToken(ApiUserPrincipal principal) {
        super(null);
        this.credentials = null;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
