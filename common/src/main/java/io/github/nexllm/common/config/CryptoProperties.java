package io.github.nexllm.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexllm.crypto")
public record CryptoProperties(Boolean enabled, String type, Aes aes) {

    public record Aes(
        String secret
    ) {

    }

}
