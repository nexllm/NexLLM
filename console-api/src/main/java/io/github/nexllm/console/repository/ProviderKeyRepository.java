package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.ProviderKeyEntity;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderKeyRepository extends CrudRepository<ProviderKeyEntity, Integer> {

    Integer countByTenantIdAndProviderIdIn(UUID tenantId, List<UUID> providerIds);

    List<ProviderKeyEntity> findByTenantId(UUID tenantId, Sort sort);

    boolean existsByTenantIdAndName(UUID tenantId, @NotBlank String name);

    void deleteByTenantIdAndProviderKeyIdIn(UUID tenantId, List<UUID> ids);

    ProviderKeyEntity findOneByTenantIdAndProviderKeyId(UUID tenantId, UUID keyId);

    boolean existsByTenantIdAndIdNotAndName(UUID tenantId, Integer id, String name);
}
