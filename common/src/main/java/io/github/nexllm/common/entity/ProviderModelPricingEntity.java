package io.github.nexllm.common.entity;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "provider_model_pricing")
public record ProviderModelPricingEntity(
    @Id
    Integer id,
    UUID tenantId,
    UUID ownerId,
    UUID providerModelId,
    String modality,
    String usageUnit,
    BigDecimal pricePerUnit,
    String currency,
    OffsetDateTime effectiveFrom
) {}

