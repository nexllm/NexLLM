package io.github.nexllm.gateway.llm.sdk.anthropic;

import io.github.nexllm.gateway.llm.api.ChatClient;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;
import io.github.nexllm.gateway.model.ModelInvocationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AnthropicChatClient extends AnthropicClient implements ChatClient {

    private final AnthropicLLMChatConverter chatConverter = new AnthropicLLMChatConverter();

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
