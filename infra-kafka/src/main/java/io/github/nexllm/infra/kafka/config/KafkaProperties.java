package io.github.nexllm.infra.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapServers,
    String schemaRegistryUrl) {

}
