package io.github.nexllm.connector.security;

import io.github.nexllm.security.model.AuthenticatedUser;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OpenApiUser(
    UUID userId,
    UUID apiKeyId,
    UUID tenantId
) implements AuthenticatedUser {

}