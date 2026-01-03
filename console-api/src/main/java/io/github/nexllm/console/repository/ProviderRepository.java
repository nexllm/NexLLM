package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.ProviderEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends CrudRepository<ProviderEntity, Integer> {

    ProviderEntity findByProviderId(UUID providerId);

    List<ProviderEntity> findByTenantIdOrderByIdDesc(UUID tenantId);

    boolean existsByNameAndTenantId(String name, UUID tenantId);

    List<ProviderEntity> findAllByTenantIdAndProviderIdIn(UUID tenantId, Collection<UUID> providerIds);

    void deleteByProviderIdIn(Collection<UUID> providerIds);

    ProviderEntity findOneByTenantIdAndProviderIdOrSystem(UUID tenantId, UUID providerId, Boolean system);
}
