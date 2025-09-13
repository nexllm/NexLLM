package io.github.nexllm.admin.model.request;

import io.github.nexllm.common.constants.ModelFeature;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateModelRequest(
    @NotBlank String name,
    @NotNull UUID providerId,
    @NotNull Boolean enabled,
    @NotEmpty List<ModelFeature> features
) {

}
