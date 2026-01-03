package io.github.nexllm.common.entity;


import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "provider_keys")
@Builder
@With
public record ProviderKeyEntity(
    @Id
    Integer id,
    UUID tenantId,
    UUID ownerId,
    UUID providerKeyId,
    UUID providerId,
    String keyEnc,
    String name,
    String description,
    Integer priority,
    Boolean enabled,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

}
