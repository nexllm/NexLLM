package io.github.nexllm.infra.redis.codec.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

public final class RedisElementMappers {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    public static final RedisElementMapper<String> STRING_MAPPER =
        new RedisElementMapper<>() {
            public String toRedis(String v) {
                return v;
            }

            public String fromRedis(String v) {
                return v;
            }
        };

    public static final RedisElementMapper<UUID> UUID_MAPPER =
        new RedisElementMapper<>() {
            public String toRedis(UUID v) {
                return v.toString();
            }

            public UUID fromRedis(String v) {
                return UUID.fromString(v);
            }
        };

    public static final RedisElementMapper<Long> LONG_MAPPER =
        new RedisElementMapper<>() {
            public String toRedis(Long v) {
                return v.toString();
            }

            public Long fromRedis(String v) {
                return Long.parseLong(v);
            }
        };

    public static <T> RedisElementMapper<T> JSON(Class<T> type) {
        return new JsonRedisElementMapper<>(DEFAULT_MAPPER, type);
    }

    public static <T> RedisElementMapper<T> JSON(
        ObjectMapper objectMapper,
        Class<T> type) {
        return new JsonRedisElementMapper<>(objectMapper, type);
    }
}

