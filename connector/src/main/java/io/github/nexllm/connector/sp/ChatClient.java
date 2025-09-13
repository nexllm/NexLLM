package io.github.nexllm.connector.sp;

import io.github.nexllm.connector.model.ModelInvocationContext;
import io.github.nexllm.connector.sp.model.chat.ChatCompletion;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionChunk;
import io.github.nexllm.connector.sp.model.chat.ChatCompletionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatClient extends LLMClient {

    Mono<ChatCompletion> chatCompletion(ModelInvocationContext context, ChatCompletionRequest request);

    Flux<ChatCompletionChunk> chatCompletionStream(ModelInvocationContext context, ChatCompletionRequest request);
}
