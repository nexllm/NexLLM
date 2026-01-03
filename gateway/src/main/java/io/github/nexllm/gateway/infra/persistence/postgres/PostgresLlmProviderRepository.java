package io.github.nexllm.gateway.infra.persistence.postgres;

import io.github.nexllm.common.entity.ProviderEntity;
import io.github.nexllm.gateway.repository.LlmProviderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PostgresLlmProviderRepository implements LlmProviderRepository {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityOperations entityOperations;

    public Mono<ProviderEntity> findByProviderId(UUID providerId) {
        return entityOperations.selectOne(Query.query(Criteria
                .where("provider_id").is(providerId)
            ), ProviderEntity.class);
    }
}
