package io.github.nexllm.gateway.infra.persistence.postgres;

import io.github.nexllm.common.entity.ProviderKeyEntity;
import io.github.nexllm.gateway.repository.LlmProviderKeyRepository;
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
public class PostgresLlmProviderKeyRepository implements LlmProviderKeyRepository {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityOperations entityOperations;

    public Mono<ProviderKeyEntity> findFirstByProviderId(UUID tenantId, UUID providerId) {
        return entityOperations.selectOne(Query.query(Criteria
            .where("tenant_id").is(tenantId)
            .and("provider_id").is(providerId)
        ).limit(1), ProviderKeyEntity.class);
    }
}
