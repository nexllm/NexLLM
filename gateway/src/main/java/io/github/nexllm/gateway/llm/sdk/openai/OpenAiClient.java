package io.github.nexllm.gateway.llm.sdk.openai;

import io.github.nexllm.gateway.llm.api.LLMClient;

public abstract class OpenAiClient implements LLMClient {

    @Override
    public final String name() {
        return "openai";
    }
}
