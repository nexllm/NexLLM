package io.github.nexllm.connector.model;

import java.util.UUID;

public record LlmModel(
    UUID providerId,
    UUID modelId,
    String name) {

}
