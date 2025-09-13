package io.github.nexllm.connector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexllm.connector")
public record ConnectorProperties(LlmProvider llmProvider) {

    public record LlmProvider(
        String baseUrl
    ) {

    }

}
