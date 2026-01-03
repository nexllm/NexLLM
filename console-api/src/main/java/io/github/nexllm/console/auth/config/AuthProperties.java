package io.github.nexllm.console.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nexllm.auth")
public record AuthProperties(Jwt jwt) {

    public record Jwt (String publicKey, String privateKey) {}
}
