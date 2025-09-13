package io.github.nexllm.connector.sp.openai;

import io.github.nexllm.connector.model.ModelInvocationContext;
import io.github.nexllm.connector.sp.ChatClient;
import io.github.nexllm.connector.sp.model.chat.ChatCompletion;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionChunk;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OpenAiChatClient implements OpenAiClient, ChatClient {

    private final OpenAiApi openAiApi;

    public OpenAiChatClient(WebClient llmWebClient) {
        this.openAiApi = new OpenAiApi(llmWebClient);
    }

    @Override
    public Mono<ChatCompletion> chatCompletion(ModelInvocationContext context, ChatCompletionRequest request) {
        return openAiApi.chatCompletion(context.meta().provider().baseUrl(),
            context.providerKey().getApiKey(),
            request);
    }

    @Override
    public Flux<ChatCompletionChunk> chatCompletionStream(ModelInvocationContext context,
        ChatCompletionRequest request) {
        return openAiApi.chatCompletionStream(context.meta().provider().baseUrl(),
            context.providerKey().getApiKey(),
            request);
    }
}
