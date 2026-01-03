package io.github.nexllm.gateway.llm.api;

import io.github.nexllm.gateway.model.ModelInvocationContext;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatClient extends LLMClient {

    Mono<ChatCompletion> chatCompletion(ModelInvocationContext context, ChatCompletionRequest request);

    Flux<ChatCompletionChunk> chatCompletionStream(ModelInvocationContext context, ChatCompletionRequest request);
}
