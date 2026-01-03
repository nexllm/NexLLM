package io.github.nexllm.infra.redis.codec.element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonRedisElementMapper<T> implements RedisElementMapper<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> type;

    JsonRedisElementMapper(ObjectMapper objectMapper, Class<T> type) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    @Override
    public String toRedis(T element) {
        try {
            return objectMapper.writeValueAsString(element);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                "Failed to serialize Redis element: " + type.getName(), e);
        }
    }

    @Override
    public T fromRedis(String value) {
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to deserialize Redis element: " + type.getName()
                    + ", value=" + value, e);
        }
    }
}
