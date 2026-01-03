package io.github.nexllm.security.auth;

import io.github.nexllm.security.jwt.JwtClaims;
import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ConsoleAuthenticationToken  extends AbstractAuthenticationToken {

        private final String credentials;
        private final ConsoleUserPrincipal principal;

        public ConsoleAuthenticationToken(String credentials) {
            super(null);
            this.principal = null;
            this.credentials = credentials;
            setAuthenticated(false);
        }

        public ConsoleAuthenticationToken(ConsoleUserPrincipal principal) {
            super(null);
            this.credentials = null;
            this.principal = principal;
            setAuthenticated(true);
        }

    public ConsoleAuthenticationToken(JwtClaims claims) {
        super(null);
        this.credentials = null;
        this.principal = ConsoleUserPrincipal.builder()
            .userId(claims.userId())
            .tenantId(claims.tenantId())
            .username(claims.username())
            .roles(claims.roles())
            .build();;
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

