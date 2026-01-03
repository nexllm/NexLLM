package io.github.nexllm.security.principal;


import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConsoleUserPrincipal(
    UUID tenantId,
    UUID userId,
    String username,
    List<String> roles
) implements AuthenticatedUserPrincipal {
}