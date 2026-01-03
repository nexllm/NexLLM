package io.github.nexllm.gateway.llm.model.speech;

import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest.AudioParameters.AudioResponseFormat;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest.AudioParameters.Voice;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public record SpeechRequest(
    @JsonProperty("model") String model,
    @JsonProperty("input") String input,
    @JsonProperty("voice") Voice voice,
    @JsonProperty("response_format") AudioResponseFormat responseFormat,
    @JsonProperty("speed") Float speed) {

}
