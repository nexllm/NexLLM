package io.github.nexllm.infra.redis.codec.collection;

import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisSetMapper<E>
    implements RedisValueMapper<Set<E>, Set<String>> {

    private final RedisElementMapper<E> elementMapper;

    public RedisSetMapper(RedisElementMapper<E> elementMapper) {
        this.elementMapper = elementMapper;
    }

    @Override
    public Set<E> fromRedis(Set<String> redisSet) {
        return redisSet.stream()
            .map(elementMapper::fromRedis)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> toRedis(Set<E> value) {
        return value.stream()
            .map(elementMapper::toRedis)
            .collect(Collectors.toSet());
    }
}

