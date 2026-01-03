package io.github.nexllm.gateway.llm.sdk.anthropic;

import io.github.nexllm.gateway.llm.api.LLMClient;

public abstract class AnthropicClient implements LLMClient {

    @Override
    public final String name() {
        return "anthropic";
    }
}
