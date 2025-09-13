package io.github.nexllm.connector.service;

import io.github.nexllm.connector.model.ModelInvocationContext;
import io.github.nexllm.connector.router.ModelRouter;
import io.github.nexllm.connector.sp.ModelClientFactory;
import io.github.nexllm.connector.sp.model.chat.ChatCompletion;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionChunk;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionRequest;
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
            .flatMap(context -> clientFactory.getChatClient(context.meta().provider().sdkClass())
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
                    .map(providerKey -> new ModelInvocationContext(modelMeta, providerKey))
            );
    }

    public Flux<ChatCompletionChunk> chatCompletionStream(ChatCompletionRequest chatCompletionRequest) {
        return createContext(chatCompletionRequest)
            .flatMapMany(context -> clientFactory.getChatClient(context.meta().provider().sdkClass())
                .flatMapMany(
                    client -> client.chatCompletionStream(context,
                        chatCompletionRequest.withModel(context.meta().model().name()))
                )
            );
    }
}
