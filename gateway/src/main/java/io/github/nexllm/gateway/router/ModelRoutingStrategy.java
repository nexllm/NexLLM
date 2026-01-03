package io.github.nexllm.gateway.router;

import io.github.nexllm.gateway.model.ModelMeta;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;


public interface ModelRoutingStrategy {

    Mono<ModelMeta.Model> select(UUID tenantId, List<ModelMeta.Model> candidates);
}
