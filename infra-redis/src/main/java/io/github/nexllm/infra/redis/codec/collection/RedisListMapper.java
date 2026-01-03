package io.github.nexllm.infra.redis.codec.collection;

import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.List;

public class RedisListMapper<E>
    implements RedisValueMapper<List<E>, List<String>> {

    private final RedisElementMapper<E> elementMapper;

    public RedisListMapper(RedisElementMapper<E> elementMapper) {
        this.elementMapper = elementMapper;
    }

    @Override
    public List<E> fromRedis(List<String> redisList) {
        return redisList.stream()
            .map(elementMapper::fromRedis)
            .toList();
    }

    @Override
    public List<String> toRedis(List<E> value) {
        return value.stream()
            .map(elementMapper::toRedis)
            .toList();
    }

    /* ===== element helpers ===== */

    public String toRedisElement(E element) {
        return elementMapper.toRedis(element);
    }

    public E fromRedisElement(String value) {
        return elementMapper.fromRedis(value);
    }
}
