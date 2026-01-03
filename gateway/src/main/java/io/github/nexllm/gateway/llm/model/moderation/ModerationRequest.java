package io.github.nexllm.gateway.llm.model.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModerationRequest(
    @JsonProperty("input") String prompt,
    @JsonProperty("model") String model
) {

}
