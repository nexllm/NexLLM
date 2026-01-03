package io.github.nexllm.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexllm.gateway")
public record GatewayProperties() {

}
