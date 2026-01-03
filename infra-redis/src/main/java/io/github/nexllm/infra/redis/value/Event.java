package io.github.nexllm.infra.redis.value;

import io.github.nexllm.infra.redis.codec.RedisValue;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record Event(UUID userId, UUID tenantId) implements RedisValue {
    public static final Mapper MAPPER = new Mapper();

    public static class Mapper implements RedisValueMapper<Event, Map<String, String>> {

        @Override
        public Event fromRedis(Map<String, String> map) {
            return new Event(
                UUID.fromString(map.get("userId")),
                UUID.fromString(map.get("tenantId"))
            );
        }

        @Override
        public Map<String, String> toRedis(Event value) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", value.userId().toString());
            map.put("tenantId", value.tenantId().toString());
            return map;
        }
    }
}
