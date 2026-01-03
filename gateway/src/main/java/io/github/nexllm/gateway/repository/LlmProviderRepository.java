package io.github.nexllm.gateway.repository;

import io.github.nexllm.common.entity.ProviderEntity;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface LlmProviderRepository {

    Mono<ProviderEntity> findByProviderId(UUID providerId);
}
