package io.github.nexllm.infra.redis.value;

import io.github.nexllm.infra.redis.codec.RedisValue;
import io.github.nexllm.infra.redis.codec.value.RedisValueMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ApiKeyMeta(UUID tenantId, UUID userId, UUID apiKeyId) implements RedisValue {

    public static final Mapper MAPPER = new Mapper();

    public static class Mapper implements RedisValueMapper<ApiKeyMeta, Map<String, String>> {

        @Override
        public ApiKeyMeta fromRedis(Map<String, String> map) {
            return new ApiKeyMeta(
                UUID.fromString(map.get("tenantId")),
                UUID.fromString(map.get("userId")),
                UUID.fromString(map.get("apiKeyId"))
            );
        }

        @Override
        public Map<String, String> toRedis(ApiKeyMeta value) {
            Map<String, String> map = new HashMap<>();
            map.put("tenantId", value.tenantId().toString());
            map.put("userId", value.userId().toString());
            map.put("apiKeyId", value.apiKeyId.toString());
            return map;
        }
    }
}
