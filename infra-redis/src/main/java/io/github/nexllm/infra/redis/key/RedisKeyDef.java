package io.github.nexllm.infra.redis.key;

import io.github.nexllm.infra.redis.codec.RedisValueType;
import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.collection.RedisListMapper;
import io.github.nexllm.infra.redis.codec.collection.RedisSetMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import io.github.nexllm.infra.redis.codec.collection.RedisZSetMapper;
import io.github.nexllm.infra.redis.codec.collection.ZSetElement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;

@RequiredArgsConstructor
public final class RedisKeyDef<T, R> {

    private final String pattern;
    private final RedisValueType valueType;
    private final RedisValueMapper<T, R> mapper;

    public static <T> RedisKeyDef<T, String> string(
        String pattern,
        RedisValueMapper<T, String> mapper) {

        return new RedisKeyDef<>(pattern, RedisValueType.STRING, mapper);
    }

    public static <T> RedisKeyDef<T, Map<String, String>> hash(
        String pattern,
        RedisValueMapper<T, Map<String, String>> mapper) {

        return new RedisKeyDef<>(pattern, RedisValueType.HASH, mapper);
    }

    public static <E> RedisKeyDef<Set<E>, Set<String>> set(
        String pattern,
        RedisElementMapper<E> elementMapper) {

        return new RedisKeyDef<>(
            pattern,
            RedisValueType.SET,
            new RedisSetMapper<>(elementMapper)
        );
    }

    public static <E> RedisKeyDef<
        Set<ZSetElement<E>>,
        Set<ZSetOperations.TypedTuple<String>>
        > zset(
        String pattern,
        RedisElementMapper<E> elementMapper) {

        return new RedisKeyDef<>(
            pattern,
            RedisValueType.ZSET,
            new RedisZSetMapper<>(elementMapper)
        );
    }

    public static <E> RedisKeyDef<List<E>, List<String>> list(
        String pattern,
        RedisElementMapper<E> elementMapper) {
        return new RedisKeyDef<>(
            pattern,
            RedisValueType.LIST,
            new RedisListMapper<>(elementMapper)
        );
    }

    public String key(Object... args) {
        return String.format(pattern, args);
    }

    public RedisValueType valueType() {
        return valueType;
    }

    public RedisValueMapper<T, R> mapper() {
        return mapper;
    }
}
