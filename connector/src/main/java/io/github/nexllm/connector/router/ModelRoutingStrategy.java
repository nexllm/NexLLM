package io.github.nexllm.connector.router;

import io.github.nexllm.connector.model.LlmModel;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;


public interface ModelRoutingStrategy {

    Mono<LlmModel> select(UUID tenantId, List<LlmModel> candidates);
}
