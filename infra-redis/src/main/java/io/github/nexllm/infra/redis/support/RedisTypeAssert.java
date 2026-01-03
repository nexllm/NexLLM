package io.github.nexllm.infra.redis.support;

import io.github.nexllm.infra.redis.codec.RedisValueType;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

public final class RedisTypeAssert {

    private RedisTypeAssert() {
    }

    public static void assertType(RedisTemplate<String, Object> redis, String key, RedisValueType expected) {
        DataType actual = redis.type(key);
        if (actual != DataType.NONE &&
            !actual.name().equalsIgnoreCase(expected.name())) {
            throw new IllegalStateException(
                "Redis key type mismatch, key=" + key +
                    ", expected=" + expected +
                    ", actual=" + actual
            );
        }
    }
}

