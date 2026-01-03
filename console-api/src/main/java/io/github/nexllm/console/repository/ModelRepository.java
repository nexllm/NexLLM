package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.ProviderModelEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends CrudRepository<ProviderModelEntity, Integer> {

    List<ProviderModelEntity> findByTenantId(UUID tenantId, Sort sort);

    ProviderModelEntity findByProviderModelId(UUID modelId);

    List<ProviderModelEntity> findByProviderId(UUID providerId);

    @Override
    List<ProviderModelEntity> findAll();

    Integer countByProviderId(UUID providerId);

    void deleteByTenantIdAndProviderModelIdIn(UUID tenantId, List<UUID> ids);

    ProviderModelEntity findOneByTenantIdAndProviderModelId(UUID tenantId, UUID modelId);

    boolean existsByTenantIdAndName(UUID tenantId, String name);

    boolean existsByTenantIdAndIdNotAndName(UUID tenantId, Integer id, String name);

    Integer countByTenantIdAndProviderModelIdIn(UUID tenantId, List<UUID> ids);

    Integer countByTenantIdAndProviderIdIn(UUID tenantId, List<UUID> providerId);
}
