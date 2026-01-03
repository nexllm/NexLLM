package io.github.nexllm.console.repository;

import io.github.nexllm.common.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    UserEntity findOneByUsername(String username);
}
