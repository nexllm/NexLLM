package io.github.nexllm.admin.model.response;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VirtualModelResponse(
    @NotNull String name,
    @NotNull UUID virtualModelId,
    @NotNull Boolean enabled,
    String description,
    @NotNull List<MappedModelResponse> models,
    @NotNull OffsetDateTime createdAt,
    @NotNull OffsetDateTime updatedAt
) {

    @Builder
    public record MappedModelResponse(
        @NotNull ModelResponse model,
        @NotNull Integer priority,
        @NotNull Integer weight
    ) {

    }

}
