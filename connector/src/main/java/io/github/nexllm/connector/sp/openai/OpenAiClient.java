package io.github.nexllm.connector.sp.openai;

import io.github.nexllm.connector.sp.LLMClient;

public interface OpenAiClient extends LLMClient {

    @Override
    default String name() {
        return "openai";
    }
}
