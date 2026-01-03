package io.github.nexllm.infra.redis.support;

import io.github.nexllm.infra.redis.codec.RedisValueType;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

public final class ReactiveRedisTypeAssert {

    private ReactiveRedisTypeAssert() {
    }

    public static Mono<Void> assertType(ReactiveStringRedisTemplate redis,
        String key,
        RedisValueType expected) {

        return redis.type(key)
            .flatMap(actual -> {
                if (actual != DataType.NONE &&
                    !actual.name().equalsIgnoreCase(expected.name())) {
                    return Mono.error(new IllegalStateException(
                        "Redis key type mismatch, key=" + key));
                }
                return Mono.empty();
            });
    }
}
