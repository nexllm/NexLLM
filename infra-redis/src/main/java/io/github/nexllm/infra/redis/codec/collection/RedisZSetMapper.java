package io.github.nexllm.infra.redis.codec.collection;

import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class RedisZSetMapper<E>
    implements RedisValueMapper<Set<ZSetElement<E>>,
        Set<TypedTuple<String>>> {

    private final RedisElementMapper<E> elementMapper;

    public RedisZSetMapper(RedisElementMapper<E> elementMapper) {
        this.elementMapper = elementMapper;
    }

    @Override
    public Set<ZSetElement<E>> fromRedis(
        Set<ZSetOperations.TypedTuple<String>> tuples) {

        return tuples.stream()
            .map(t -> new ZSetElement<>(
                elementMapper.fromRedis(t.getValue()),
                t.getScore()
            ))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> toRedis(
        Set<ZSetElement<E>> value) {

        return value.stream()
            .map(e -> ZSetOperations.TypedTuple.of(
                elementMapper.toRedis(e.value()),
                e.score()
            ))
            .collect(Collectors.toSet());
    }
}
