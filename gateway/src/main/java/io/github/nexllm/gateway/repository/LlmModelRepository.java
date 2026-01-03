package io.github.nexllm.gateway.repository;

import io.github.nexllm.common.entity.ProviderModelEntity;
import java.util.List;
import reactor.core.publisher.Mono;

public interface LlmModelRepository {


    Mono<List<ProviderModelEntity>> findAll();
}
