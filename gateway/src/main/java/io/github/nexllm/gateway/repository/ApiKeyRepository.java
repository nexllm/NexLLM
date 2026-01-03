package io.github.nexllm.gateway.repository;

import io.github.nexllm.common.entity.VirtualKeyEntity;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ApiKeyRepository {

    Mono<VirtualKeyEntity> findByKey(String keyHash);

    Mono<Boolean> existsByTenantIdAndVirtualKeyId(UUID tenantId, UUID virtualKeyId);
}
