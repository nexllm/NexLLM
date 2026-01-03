package io.github.nexllm.gateway.router;

import io.github.nexllm.gateway.model.ModelMeta;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FirstModelRoutingStrategy implements ModelRoutingStrategy {

    @Override
    public Mono<ModelMeta.Model> select(UUID tenantId, List<ModelMeta.Model> candidates) {
        return Mono.just(candidates.get(0));
    }
}
