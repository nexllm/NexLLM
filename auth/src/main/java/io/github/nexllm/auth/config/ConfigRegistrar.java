package io.github.nexllm.auth.config;

import io.github.nexllm.common.config.CryptoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    CryptoProperties.class,
    AuthProperties.class
})
public class ConfigRegistrar {

}
