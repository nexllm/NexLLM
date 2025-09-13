package io.github.nexllm.common.config;

import io.github.nexllm.common.crypto.CryptoProvider;
import io.github.nexllm.common.crypto.impl.AesCryptoProvider;
import io.github.nexllm.common.crypto.impl.NoOpCryptoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CryptoProperties.class)
@ConditionalOnBooleanProperty(value = "nexllm.crypto.enabled")
public class CryptoConfig {

    private final CryptoProperties cryptoProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "nexllm.crypto.type", havingValue = "AES")
    public CryptoProvider aesCryptoProvider() {
        return new AesCryptoProvider(cryptoProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "nexllm.crypto.type", havingValue = "AES")
    public CryptoProvider noopProvider() {
        return new NoOpCryptoProvider();
    }
}
