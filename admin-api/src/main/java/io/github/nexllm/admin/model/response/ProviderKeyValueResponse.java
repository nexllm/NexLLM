package io.github.nexllm.admin.model.response;

import jakarta.validation.constraints.NotNull;

public record ProviderKeyValueResponse(
    @NotNull String key
) {

}
