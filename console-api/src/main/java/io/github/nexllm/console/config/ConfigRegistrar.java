package io.github.nexllm.console.config;

import io.github.nexllm.common.config.CryptoProperties;
import io.github.nexllm.console.auth.config.AuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    CryptoProperties.class,
    ConsoleProperties.class,
    AuthProperties.class
})
public class ConfigRegistrar {

}
