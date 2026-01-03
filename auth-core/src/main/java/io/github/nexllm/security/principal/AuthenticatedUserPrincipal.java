package io.github.nexllm.security.principal;

import java.util.UUID;

public interface AuthenticatedUserPrincipal {

    UUID userId();

    UUID tenantId();
}
