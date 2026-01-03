package io.github.nexllm.console.service;

import io.github.nexllm.common.constants.EntityStatus;
import io.github.nexllm.common.entity.VirtualKeyEntity;
import io.github.nexllm.console.model.request.CreateVirtualKeyRequest;
import io.github.nexllm.console.model.response.VirtualKeyResponse;
import io.github.nexllm.console.repository.VirtualKeyRepository;
import io.github.nexllm.infra.redis.accessor.RedisAccessor;
import io.github.nexllm.infra.redis.key.RedisKeys;
import io.github.nexllm.infra.redis.value.ApiKeyMeta;
import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import io.github.nexllm.security.util.ApiKeyUtils;
import io.github.nexllm.security.util.SecurityContextUtils;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VirtualKeyService {

    private final RedisAccessor redisAccessor;
    private final VirtualKeyRepository virtualKeyRepository;

    public List<VirtualKeyResponse> getKeys() {
        return virtualKeyRepository.findAll(Sort.by(Order.desc("id"))).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public VirtualKeyResponse createKey(CreateVirtualKeyRequest createKeyRequest) {
        ConsoleUserPrincipal currentUser = SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class);
        String key = ApiKeyUtils.newKey();
        VirtualKeyEntity entity = VirtualKeyEntity.builder()
            .virtualKeyId(UUID.randomUUID())
            .status(EntityStatus.ACTIVE.getCode())
            .name(createKeyRequest.name())
            .keyHash(ApiKeyUtils.hash(key))
            .userId(currentUser.userId())
            .tenantId(currentUser.tenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        entity = virtualKeyRepository.save(entity);
        redisAccessor.set(RedisKeys.API_KEY,
            new ApiKeyMeta(currentUser.tenantId(), currentUser.userId(), entity.virtualKeyId()));
        return mapToResponse(entity)
            .withKey(key);
    }

    private VirtualKeyResponse mapToResponse(VirtualKeyEntity entity) {
        return VirtualKeyResponse
            .builder()
            .virtualKeyId(entity.virtualKeyId())
            .name(entity.name())
            .status(entity.status())
            .allowedModels(entity.allowedModels())
            .expireAt(entity.expireAt())
            .createdAt(entity.createdAt())
            .updatedAt(entity.updatedAt())
            .build();
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        List<UUID> deletedIds = virtualKeyRepository.deleteByIds(SecurityContextUtils.getTenantId(), ids);
    }
}
