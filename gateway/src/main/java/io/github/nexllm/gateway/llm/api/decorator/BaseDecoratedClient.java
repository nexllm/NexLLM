package io.github.nexllm.gateway.llm.api.decorator;

import io.github.nexllm.gateway.llm.api.LLMClient;

public abstract class BaseDecoratedClient<T extends LLMClient> implements LLMClient {

    protected T delegate;

    @Override
    public String name() {
        return delegate.name();
    }
}
