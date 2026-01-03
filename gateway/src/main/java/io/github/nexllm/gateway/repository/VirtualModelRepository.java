package io.github.nexllm.gateway.repository;

import io.github.nexllm.common.entity.ProviderModelEntity;
import io.github.nexllm.common.entity.VirtualModelEntity;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface VirtualModelRepository {


    Mono<VirtualModelEntity> findByName(UUID tenantId, String modelName);

    Mono<List<ProviderModelEntity>> findModelsByVirtualModelId(UUID tenantId, UUID virtualModelId);
}
