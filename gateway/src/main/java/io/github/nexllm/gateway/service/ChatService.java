package io.github.nexllm.gateway.service;

import io.github.nexllm.gateway.model.ModelInvocationContext;
import io.github.nexllm.gateway.router.ModelRouter;
import io.github.nexllm.gateway.llm.api.ModelClientFactory;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletion;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionChunk;
import io.github.nexllm.gateway.llm.model.chat.ChatCompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ModelRouter modelRouter;
    private final ModelClientFactory clientFactory;

    public Mono<ChatCompletion> chatCompletion(ChatCompletionRequest chatCompletionRequest) {
        return createContext(chatCompletionRequest)
            .flatMap(context -> clientFactory.getChatClient(context.meta().provider().providerType())
                .flatMap(
                    client -> client.chatCompletion(context,
                        chatCompletionRequest.withModel(context.meta().model().name()))
                )
            );
    }

    private Mono<ModelInvocationContext> createContext(ChatCompletionRequest chatCompletionRequest) {
        return modelRouter.resolveMeta(chatCompletionRequest.model())
            .flatMap(modelMeta ->
                modelRouter.selectKey(modelMeta.provider().providerId(), modelMeta.model().modelId())
                    .map(providerKey -> new ModelInvocationContext(modelMeta.withProviderKey(providerKey)))
            );
    }

    public Flux<ChatCompletionChunk> chatCompletionStream(ChatCompletionRequest chatCompletionRequest) {
        return createContext(chatCompletionRequest)
            .flatMapMany(context -> clientFactory.getChatClient(context.meta().provider().providerType())
                .flatMapMany(
                    client -> client.chatCompletionStream(context,
                        chatCompletionRequest.withModel(context.meta().model().name()))
                )
            );
    }
}
