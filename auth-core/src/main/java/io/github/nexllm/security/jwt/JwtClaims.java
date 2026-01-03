package io.github.nexllm.security.jwt;


import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record JwtClaims(
    UUID tenantId,
    UUID userId,
    @Nullable UUID apiKeyId,
    String username,
    List<String> roles,
    PrincipalSource source
) {

}
