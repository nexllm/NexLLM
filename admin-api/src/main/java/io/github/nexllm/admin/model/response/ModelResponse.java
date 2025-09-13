package io.github.nexllm.admin.model.response;

import io.github.nexllm.common.constants.ModelFeature;
import io.github.nexllm.common.constants.ModelStatus;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record ModelResponse(
    @NotNull UUID modelId,
    @NotNull ProviderResponse provider,
    @NotNull String name,
    String description,
    @NotNull List<ModelFeature> features,
    @NotNull Integer contextLength,
    @NotNull ModelStatus status,
    @NotNull Integer maxOutputTokens,
    @NotNull Boolean enabled,
    Map<String, Object> defaultParams,
    @NotNull OffsetDateTime createdAt,
    @NotNull OffsetDateTime updatedAt
) {

}
