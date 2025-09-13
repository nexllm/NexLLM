package io.github.nexllm.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nexllm.admin")
public record AdminProperties() {

}
