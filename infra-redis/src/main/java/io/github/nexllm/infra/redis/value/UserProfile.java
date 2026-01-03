package io.github.nexllm.infra.redis.value;

import io.github.nexllm.infra.redis.codec.RedisValue;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record UserProfile(
    UUID tenantId,
    UUID userId) implements RedisValue {

    public static final Mapper MAPPER = new Mapper();

    public static class Mapper implements RedisValueMapper<UserProfile, Map<String, String>> {

        @Override
        public UserProfile fromRedis(Map<String, String> map) {
            return new UserProfile(
                UUID.fromString(map.get("tenantId")),
                UUID.fromString(map.get("userId"))
            );
        }

        @Override
        public Map<String, String> toRedis(UserProfile value) {
            Map<String, String> map = new HashMap<>();
            map.put("tenantId", value.tenantId().toString());
            map.put("userId", value.userId().toString());
            return map;
        }
    }
}
