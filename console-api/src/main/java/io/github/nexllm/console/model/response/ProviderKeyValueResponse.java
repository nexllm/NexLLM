package io.github.nexllm.console.model.response;

import jakarta.validation.constraints.NotNull;

public record ProviderKeyValueResponse(
    @NotNull String key
) {

}
