package io.github.nexllm.connector.model;

public record ModelInvocationContext(
    ModelMeta meta,
    LlmProviderKey providerKey
) {
}
