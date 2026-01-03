package io.github.nexllm.gateway.llm.converter;

import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;

public interface LLMChatConverter<T, E> {

    T convertRequest(final ChatCompletionRequest request);

    ChatCompletion convertResponse(final E response);

    ChatCompletionChunk convertChunkResponse(final E response);

}
