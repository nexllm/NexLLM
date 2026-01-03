package io.github.nexllm.infra.redis.accessor;

import io.github.nexllm.infra.redis.codec.collection.RedisListMapper;
import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import io.github.nexllm.infra.redis.key.RedisKeyDef;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@RequiredArgsConstructor
public class RedisAccessor {

    private final StringRedisTemplate redis;

    public <T, R> T get(RedisKeyDef<T, R> keyDef, Object... args) {
        String key = keyDef.key(args);

        return switch (keyDef.valueType()) {
            case STRING -> {
                String v = redis.opsForValue().get(key);
                yield v == null
                    ? null
                    : ((RedisValueMapper<T, String>) keyDef.mapper()).fromRedis(v);
            }

            case HASH -> {
                Map<Object, Object> entries = redis.opsForHash().entries(key);
                if (entries == null || entries.isEmpty()) {
                    yield null;
                }
                Map<String, String> map = entries.entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> (String) e.getValue()
                    ));
                yield ((RedisValueMapper<T, Map<String, String>>) keyDef.mapper())
                    .fromRedis(map);
            }

            case SET -> {
                Set<String> members = redis.opsForSet().members(key);
                yield members == null || members.isEmpty()
                    ? null
                    : ((RedisValueMapper<T, Set<String>>) keyDef.mapper())
                        .fromRedis(members);
            }

            default -> throw new UnsupportedOperationException(
                "Unsupported get for: " + keyDef.valueType()
            );
        };
    }

    public <T, R> void set(RedisKeyDef<T, R> keyDef, T value, Object... args) {
        String key = keyDef.key(args);
        RedisValueMapper<T, R> mapper = keyDef.mapper();

        switch (keyDef.valueType()) {
            case STRING ->
                redis.opsForValue()
                    .set(key, (String) mapper.toRedis(value));

            case HASH ->
                redis.opsForHash()
                    .putAll(key, (Map<String, String>) mapper.toRedis(value));

            case SET ->
                redis.opsForSet()
                    .add(key,
                        ((Set<String>) mapper.toRedis(value))
                            .toArray(String[]::new));

            case ZSET ->
                redis.opsForZSet()
                    .add(key,
                        (Set<ZSetOperations.TypedTuple<String>>) mapper.toRedis(value));

            default ->
                throw new UnsupportedOperationException(
                    "Unsupported set for: " + keyDef.valueType()
                );
        }
    }

    public boolean delete(RedisKeyDef<?, ?> keyDef, Object... args) {
        return redis.delete(keyDef.key(args));
    }

    // ===== Set helpers =====

    public <E> Set<E> members(
        RedisKeyDef<Set<E>, ?> keyDef, Object... args) {

        String key = keyDef.key(args);
        RedisElementMapper<E> mapper =
            (RedisElementMapper<E>) keyDef.mapper();

        Set<String> members = redis.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        return members.stream()
            .map(mapper::fromRedis)
            .collect(Collectors.toSet());
    }

    public <E> boolean addToSet(
        RedisKeyDef<Set<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisElementMapper<E> mapper =
            (RedisElementMapper<E>) keyDef.mapper();

        Long added = redis.opsForSet()
            .add(key, mapper.toRedis(element));

        return added != null && added > 0;
    }

    public <E> boolean removeFromSet(
        RedisKeyDef<Set<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisElementMapper<E> mapper =
            (RedisElementMapper<E>) keyDef.mapper();

        Long removed = redis.opsForSet()
            .remove(key, mapper.toRedis(element));

        return removed != null && removed > 0;
    }

    // ===== List helpers =====

    public <E> Long leftPush(
        RedisKeyDef<List<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper =
            (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .leftPush(key, mapper.toRedisElement(element));
    }

    public <E> Long rightPush(
        RedisKeyDef<List<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper =
            (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .rightPush(key, mapper.toRedisElement(element));
    }

    public <E> E leftPop(
        RedisKeyDef<List<E>, ?> keyDef, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper =
            (RedisListMapper<E>) keyDef.mapper();

        String v = redis.opsForList().leftPop(key);
        return v == null ? null : mapper.fromRedisElement(v);
    }

    public <E> E rightPop(
        RedisKeyDef<List<E>, ?> keyDef, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper =
            (RedisListMapper<E>) keyDef.mapper();

        String v = redis.opsForList().rightPop(key);
        return v == null ? null : mapper.fromRedisElement(v);
    }

    public <E> List<E> range(
        RedisKeyDef<List<E>, ?> keyDef,
        long start,
        long end,
        Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper =
            (RedisListMapper<E>) keyDef.mapper();

        List<String> values = redis.opsForList()
            .range(key, start, end);

        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        return values.stream()
            .map(mapper::fromRedisElement)
            .collect(Collectors.toList());
    }

    public Long size(
        RedisKeyDef<List<?>, ?> keyDef, Object... args) {

        return redis.opsForList().size(keyDef.key(args));
    }
}
