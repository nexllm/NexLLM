package io.github.nexllm.gateway.repository;

import io.github.nexllm.common.entity.ProviderKeyEntity;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface LlmProviderKeyRepository {


    Mono<ProviderKeyEntity> findFirstByProviderId(UUID tenantId, UUID providerId);
}
