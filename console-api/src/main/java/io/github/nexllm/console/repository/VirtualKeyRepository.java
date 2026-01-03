package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.VirtualKeyEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualKeyRepository extends CrudRepository<VirtualKeyEntity, Integer> {

    List<VirtualKeyEntity> findAll(Sort sort);

    boolean existsByTenantIdAndName(UUID tenantId, String name);

    List<VirtualKeyEntity> findAllByTenantIdAndVirtualKeyIdIn(UUID tenantId, Collection<UUID> virtualKeyId);

    VirtualKeyEntity findAllByTenantIdAndVirtualKeyId(UUID tenantId, UUID virtualKeyId);

    @Query("""
        DELETE FROM virtual_keys
        WHERE tenant_id = :tenantId
          AND virtual_key_id IN :ids
        RETURNING virtual_key_id
        """)
    List<UUID> deleteByIds(UUID tenantId, List<UUID> ids);
}
