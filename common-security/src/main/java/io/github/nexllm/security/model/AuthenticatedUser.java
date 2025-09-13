package io.github.nexllm.security.model;

import java.util.UUID;

public interface AuthenticatedUser {

    UUID userId();

    UUID tenantId();
}
