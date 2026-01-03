package io.github.nexllm.infra.redis.codec.value;

public interface RedisValueMapper<T, R> {

    T fromRedis(R redisValue);

    R toRedis(T value);
}

