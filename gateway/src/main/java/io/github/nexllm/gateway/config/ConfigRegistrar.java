package io.github.nexllm.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    GatewayProperties.class,
    AuthProperties.class
})
public class ConfigRegistrar {

}
