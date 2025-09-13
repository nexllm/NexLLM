package io.github.nexllm.admin.service;

import io.github.nexllm.admin.model.request.CreateProviderKeyRequest;
import io.github.nexllm.admin.model.request.PatchProviderKeyRequest;
import io.github.nexllm.admin.model.response.ProviderKeyResponse;
import io.github.nexllm.admin.model.response.ProviderKeyValueResponse;
import io.github.nexllm.admin.repository.ProviderKeyRepository;
import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.crypto.CryptoProvider;
import io.github.nexllm.common.entity.LlmProviderKeyEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.security.model.AuthUser;
import io.github.nexllm.security.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderKeyService {

    private final CryptoProvider cryptoProvider;
    private final ProviderService providerService;
    private final ProviderKeyRepository providerKeyRepository;

    public List<ProviderKeyResponse> getProviderKeys(UUID providerId) {
        return providerKeyRepository.findByTenantId(SecurityUtils.getTenantId(), Sort.by(Order.desc("id")))
            .stream()
            .filter(it -> providerId == null || it.providerId().equals(providerId))
            .map(this::mapToResponse)
            .toList();
    }

    private ProviderKeyResponse mapToResponse(LlmProviderKeyEntity entity) {
        return ProviderKeyResponse.builder()
            .providerKeyId(entity.providerKeyId())
            .enabled(entity.enabled())
            .name(entity.name())
            .description(entity.description())
            .provider(providerService.findByProviderId(entity.providerId()))
            .priority(entity.priority())
            .createdAt(entity.createdAt())
            .updatedAt(entity.updatedAt())
            .build();
    }

    public ProviderKeyResponse createProviderKey(@Valid CreateProviderKeyRequest createRequest) {
        if (providerKeyRepository.existsByTenantIdAndName(SecurityUtils.getTenantId(), createRequest.name())) {
            throw new BizException(ErrorCode.ADM_CONFLICT, createRequest.name());
        }
        LlmProviderKeyEntity providerKeyEntity = LlmProviderKeyEntity.builder()
            .providerKeyId(UUID.randomUUID())
            .providerId(createRequest.providerId())
            .name(createRequest.name())
            .enabled(createRequest.enabled())
            .priority(createRequest.priority())
            .description(createRequest.description())
            .keyEnc(cryptoProvider.encrypt(createRequest.key()))
            .userId(SecurityUtils.getCurrentUser(AuthUser.class).userId())
            .tenantId(SecurityUtils.getTenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        providerKeyRepository.save(providerKeyEntity);
        return mapToResponse(providerKeyEntity);
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        providerKeyRepository.deleteByTenantIdAndProviderKeyIdIn(
            SecurityUtils.getTenantId(), ids);
    }

    public ProviderKeyResponse patchProviderKey(UUID providerKeyId,
        @Valid PatchProviderKeyRequest patchRequest) {
        LlmProviderKeyEntity providerKeyEntity = providerKeyRepository.findOneByTenantIdAndProviderKeyId(
            SecurityUtils.getTenantId(), providerKeyId);
        if (patchRequest.enabled() != null) {
            providerKeyEntity = providerKeyEntity.withEnabled(patchRequest.enabled());
        }
        if (patchRequest.name() != null) {
            boolean exists = providerKeyRepository.existsByTenantIdAndIdNotAndName(SecurityUtils.getTenantId(),
                providerKeyEntity.id(), patchRequest.name());
            if (exists) {
                throw new BizException(ErrorCode.ADM_CONFLICT, patchRequest.name());
            }
            providerKeyEntity = providerKeyEntity.withName(patchRequest.name());
        }
        if (patchRequest.key() != null) {
            providerKeyEntity = providerKeyEntity.withKeyEnc(cryptoProvider.encrypt(patchRequest.key()));
        }
        if (patchRequest.priority() != null) {
            providerKeyEntity = providerKeyEntity.withPriority(patchRequest.priority());
        }
        if (patchRequest.description() != null) {
            providerKeyEntity = providerKeyEntity.withDescription(patchRequest.description());
        }
        providerKeyRepository.save(providerKeyEntity);
        return mapToResponse(providerKeyEntity);
    }

    public ProviderKeyValueResponse getProviderKeyValue(UUID providerKeyId) {
        LlmProviderKeyEntity entity = providerKeyRepository.findOneByTenantIdAndProviderKeyId(
            SecurityUtils.getTenantId(), providerKeyId);
        return new ProviderKeyValueResponse(cryptoProvider.decrypt(entity.keyEnc()));
    }
}
