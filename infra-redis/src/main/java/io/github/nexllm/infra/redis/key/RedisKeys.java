package io.github.nexllm.infra.redis.key;

import io.github.nexllm.infra.redis.codec.element.RedisElementMappers;
import io.github.nexllm.infra.redis.codec.collection.ZSetElement;
import io.github.nexllm.infra.redis.value.ApiKeyMeta;
import io.github.nexllm.infra.redis.value.Event;
import io.github.nexllm.infra.redis.value.UserProfile;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.redis.core.ZSetOperations;

public final class RedisKeys {

    private static final String PREFIX = "nexllm:v1";

    // apikey hash -> ApiKeyMeta
    public static final RedisKeyDef<ApiKeyMeta, Map<String, String>> API_KEY =
        RedisKeyDef.hash(PREFIX + ":auth:apikey:%s", ApiKeyMeta.MAPPER);

    // userId -> UserProfile
    public static final RedisKeyDef<UserProfile, Map<String, String>> USER_PROFILE =
        RedisKeyDef.hash(PREFIX + ":auth:user:%s", UserProfile.MAPPER);

    // user -> apiKey hashes
    public static final RedisKeyDef<Set<UUID>, Set<String>> USER_APIKEYS =
        RedisKeyDef.set(PREFIX + ":auth:user:%s:apikeys", RedisElementMappers.UUID_MAPPER);

    public static final RedisKeyDef<
        Set<ZSetElement<String>>,
        Set<ZSetOperations.TypedTuple<String>>
        > USER_REQUESTS = RedisKeyDef.zset(
        "rate:user:%s:requests",
        RedisElementMappers.STRING_MAPPER
    );

    public static final RedisKeyDef<List<Event>, List<String>> USER_EVENTS =
        RedisKeyDef.list(
            "event:user:%s",
            RedisElementMappers.JSON(Event.class)
        );


    private RedisKeys() {
    }
}
