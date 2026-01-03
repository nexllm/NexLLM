package io.github.nexllm.common.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@With
@Builder
@Table(name = "provider_models")
public record ProviderModelEntity(
    @Id
    Integer id,
    UUID tenantId,
    UUID ownerId,
    UUID providerId,
    UUID providerModelId,
    String name,
    String status,
    List<String> features,
    Integer contextLength,
    Integer maxOutputTokens,
    Boolean enabled,
    Map<String, Object> defaultParams,
    OffsetDateTime lastCheckedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

}
