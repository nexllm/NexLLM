package io.github.nexllm.gateway.llm.api.decorator;

import io.github.nexllm.gateway.llm.api.ChatClient;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;
import io.github.nexllm.gateway.model.ModelInvocationContext;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DecoratedLLMChatClient extends BaseDecoratedClient<ChatClient> implements ChatClient {

    @Override
    public Mono<ChatCompletion> chatCompletion(ModelInvocationContext context, ChatCompletionRequest request) {
        return null;
    }

    @Override
    public Flux<ChatCompletionChunk> chatCompletionStream(ModelInvocationContext context,
        ChatCompletionRequest request) {
        return null;
    }
}
