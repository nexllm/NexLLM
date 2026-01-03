package io.github.nexllm.gateway.infra.persistence.postgres;

import io.github.nexllm.common.entity.ProviderModelEntity;
import io.github.nexllm.gateway.repository.LlmModelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PostgresLlmModelRepository implements LlmModelRepository {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityOperations entityOperations;

    public Mono<List<ProviderModelEntity>> findAll() {
        return entityOperations.select(Query.query(Criteria
                .where("enabled").is(true)
            ), ProviderModelEntity.class)
            .collectList();
    }
}
