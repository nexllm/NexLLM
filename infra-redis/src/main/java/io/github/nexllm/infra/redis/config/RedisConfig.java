package io.github.nexllm.infra.redis.config;

import io.github.nexllm.infra.redis.accessor.ReactiveRedisAccessor;
import io.github.nexllm.infra.redis.accessor.RedisAccessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Configuration
    @ConditionalOnMissingBean(ReactiveStringRedisTemplate.class)
    static class ServletRedisConfig {

        @Bean
        public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
            return new StringRedisTemplate(factory);
        }

        @Bean
        public RedisAccessor redisAccessor(StringRedisTemplate redisTemplate) {
            return new RedisAccessor(redisTemplate);
        }
    }

    // ===== WebFlux / Reactive =====
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    static class ReactiveRedisConfig {

        @Bean
        public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
            return new ReactiveStringRedisTemplate(factory);
        }

        @Bean
        public ReactiveRedisAccessor reactiveRedisAccessor(ReactiveStringRedisTemplate redisTemplate) {
            return new ReactiveRedisAccessor(redisTemplate);
        }
    }
}
