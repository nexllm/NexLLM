package io.github.nexllm.event.processor.cache;

import io.github.nexllm.infra.kafka.constant.KafkaTopics;
import io.github.nexllm.infra.redis.accessor.RedisAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheUpdater {

    private final RedisAccessor redis;

    @KafkaListener(
        topics = KafkaTopics.IAM_USER_EVENTS,
        groupId = "event-processor.user-cache"
    )
    public void onUserUpserted(String message) {
    }
}
