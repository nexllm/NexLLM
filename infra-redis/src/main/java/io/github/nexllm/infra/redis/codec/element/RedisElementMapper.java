package io.github.nexllm.infra.redis.codec.element;

public interface RedisElementMapper<E> {

    String toRedis(E element);

    E fromRedis(String value);
}

