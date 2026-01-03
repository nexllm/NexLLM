package io.github.nexllm.infra.redis.codec.collection;

public record ZSetElement<E>(
    E value,
    double score
) {}

