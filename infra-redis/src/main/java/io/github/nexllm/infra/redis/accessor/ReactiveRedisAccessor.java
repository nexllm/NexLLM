package io.github.nexllm.infra.redis.accessor;

import io.github.nexllm.infra.redis.key.RedisKeyDef;
import io.github.nexllm.infra.redis.codec.element.RedisElementMapper;
import io.github.nexllm.infra.redis.codec.collection.RedisListMapper;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReactiveRedisAccessor {

    private final ReactiveStringRedisTemplate redis;

    public <T, R> Mono<T> get(RedisKeyDef<T, R> keyDef, Object... args) {
        String key = keyDef.key(args);

        return switch (keyDef.valueType()) {
            case STRING -> redis.opsForValue()
                .get(key)
                .map(v -> ((RedisValueMapper<T, String>) keyDef.mapper())
                    .fromRedis(v));

            case HASH -> redis.opsForHash()
                .entries(key)
                .collectMap(
                    e -> (String) e.getKey(),
                    e -> (String) e.getValue()
                )
                .map(m -> ((RedisValueMapper<T, Map<String, String>>) keyDef.mapper())
                    .fromRedis(m))
                .filter(v -> !((Map<?, ?>) v).isEmpty());

            case SET -> redis.opsForSet()
                .members(key)
                .collect(Collectors.toSet())
                .map(s -> ((RedisValueMapper<T, Set<String>>) keyDef.mapper())
                    .fromRedis(s));

//            case ZSET -> redis.opsForZSet()
//                // get all data from the first element(0) to the last element(-1)
//                .rangeWithScores(key, Range.<Long>from(Bound.inclusive(0L)).to(Bound.inclusive(-1L)))
//                .collect(Collectors.toSet())
//                .map(z -> ((RedisValueMapper<T, Set<ZSetOperations.TypedTuple<String>>>) keyDef.mapper())
//                    .fromRedis(z));
            default ->
                Mono.error(new UnsupportedOperationException("Unsupported operation for: " + keyDef.valueType()));
        };
    }

    public <T, R> Mono<Void> set(RedisKeyDef<T, R> keyDef, T value, Object... args) {
        String key = keyDef.key(args);
        RedisValueMapper<T, R> mapper = keyDef.mapper();

        return switch (keyDef.valueType()) {
            case STRING -> redis.opsForValue()
                .set(key, (String) mapper.toRedis(value))
                .then();

            case HASH -> redis.opsForHash()
                .putAll(key, (Map<String, String>) mapper.toRedis(value))
                .then();

            case SET -> redis.opsForSet()
                .add(key, ((Set<String>) mapper.toRedis(value))
                    .toArray(String[]::new))
                .then();

            case ZSET -> redis.opsForZSet()
                .addAll(key,
                    (Set<ZSetOperations.TypedTuple<String>>) mapper.toRedis(value))
                .then();
            default ->
                Mono.error(new UnsupportedOperationException("Unsupported operation for: " + keyDef.valueType()));
        };
    }


    public <T, R> Mono<Boolean> delete(RedisKeyDef<T, R> keyDef, Object... args) {
        return redis.delete(keyDef.key(args)).map(v -> v > 0);
    }

    // ===== Set helpers =====


    public <E> Flux<E> members(RedisKeyDef<Set<E>, ?> keyDef, Object... args) {
        String key = keyDef.key(args);
        RedisElementMapper<E> mapper = (RedisElementMapper<E>) keyDef.mapper();

        return redis.opsForSet()
            .members(key)
            .map(mapper::fromRedis);
    }


    public <E> Mono<Boolean> addToSet(RedisKeyDef<Set<E>, ?> keyDef, E element, Object... args) {
        String key = keyDef.key(args);
        RedisElementMapper<E> mapper = (RedisElementMapper<E>) keyDef.mapper();

        return redis.opsForSet()
            .add(key, mapper.toRedis(element))
            .map(v -> v > 0);
    }


    public <E> Mono<Boolean> removeFromSet(RedisKeyDef<Set<E>, ?> keyDef, E element, Object... args) {
        String key = keyDef.key(args);
        RedisElementMapper<E> mapper = (RedisElementMapper<E>) keyDef.mapper();

        return redis.opsForSet()
            .remove(key, mapper.toRedis(element))
            .map(v -> v > 0);
    }


    public <E> Mono<Long> leftPush(
        RedisKeyDef<List<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper = (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .leftPush(key, mapper.toRedisElement(element));
    }


    public <E> Mono<Long> rightPush(
        RedisKeyDef<List<E>, ?> keyDef, E element, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper = (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .rightPush(key, mapper.toRedisElement(element));
    }


    public <E> Mono<E> leftPop(
        RedisKeyDef<List<E>, ?> keyDef, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper = (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .leftPop(key)
            .map(mapper::fromRedisElement);
    }


    public <E> Mono<E> rightPop(
        RedisKeyDef<List<E>, ?> keyDef, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper = (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .rightPop(key)
            .map(mapper::fromRedisElement);
    }


    public <E> Flux<E> range(
        RedisKeyDef<List<E>, ?> keyDef, long start, long end, Object... args) {

        String key = keyDef.key(args);
        RedisListMapper<E> mapper = (RedisListMapper<E>) keyDef.mapper();

        return redis.opsForList()
            .range(key, start, end)
            .map(mapper::fromRedisElement);
    }

    public Mono<Long> size(
        RedisKeyDef<List<?>, List<String>> keyDef, Object... args) {

        return redis.opsForList().size(keyDef.key(args));
    }

}
