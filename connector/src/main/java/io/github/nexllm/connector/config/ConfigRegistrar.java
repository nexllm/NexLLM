package io.github.nexllm.connector.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    ConnectorProperties.class
})
public class ConfigRegistrar {

}
