package io.github.nexllm.common.entity;


import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "providers")
@Builder
public record ProviderEntity(
    @Id
    Integer id,
    UUID tenantId,
    UUID ownerId,
    UUID providerId,
    String name,
    String baseUrl,
    String description,
    String providerType,
    Boolean system,
    Boolean enabled,
    Map<String, Object> extraConfig,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

}

