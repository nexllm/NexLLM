package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.TenantEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends CrudRepository<TenantEntity, Integer> {

}
