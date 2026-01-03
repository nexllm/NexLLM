package io.github.nexllm.security.principal;

import io.github.nexllm.security.jwt.PrincipalSource;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ApiUserPrincipal(
    UUID tenantId,
    UUID userId,
    UUID apiKeyId,
    PrincipalSource source
) implements AuthenticatedUserPrincipal {
}