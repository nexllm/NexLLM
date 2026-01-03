package io.github.nexllm.gateway.llm.sdk.anthropic;

import io.github.nexllm.gateway.llm.sdk.anthropic.model.AnthropicRequest;
import io.github.nexllm.gateway.llm.sdk.anthropic.model.AnthropicResponse;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;
import io.github.nexllm.gateway.llm.converter.LLMChatConverter;

public class AnthropicLLMChatConverter implements LLMChatConverter<AnthropicRequest, AnthropicResponse> {

    @Override
    public AnthropicRequest convertRequest(ChatCompletionRequest request) {
        return null;
    }

    @Override
    public ChatCompletion convertResponse(AnthropicResponse response) {
        return null;
    }

    @Override
    public ChatCompletionChunk convertChunkResponse(AnthropicResponse response) {
        return null;
    }
}
