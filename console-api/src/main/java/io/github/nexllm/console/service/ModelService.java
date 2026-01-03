package io.github.nexllm.console.service;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.constants.ModelFeature;
import io.github.nexllm.common.constants.ModelStatus;
import io.github.nexllm.common.entity.ProviderModelEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.console.model.request.CreateModelRequest;
import io.github.nexllm.console.model.request.PatchModelRequest;
import io.github.nexllm.console.model.response.ModelResponse;
import io.github.nexllm.console.repository.ModelRepository;
import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import io.github.nexllm.security.util.SecurityContextUtils;
import jakarta.validation.Valid;
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
public class ModelService {

    private final ModelRepository modelRepository;
    private final ProviderService providerService;

    public List<ModelResponse> getModels(UUID providerId, String status) {
        return modelRepository.findByTenantId(SecurityContextUtils.getTenantId(), Sort.by(Order.desc("id")))
            .stream()
            .filter(it -> providerId == null || it.providerId().equals(providerId))
            .filter(it -> status == null || it.status().equals(status))
            .map(this::mapToResponse)
            .toList();
    }

    public ModelResponse findByModelId(UUID modelId) {
        ProviderModelEntity modelEntity = modelRepository.findByProviderModelId(modelId);
        return mapToResponse(modelEntity);
    }

    public List<ModelResponse> getProviderModels(UUID providerId) {
        return modelRepository.findByProviderId(providerId)
            .stream()
            .map(this::mapToResponseWithoutProvider)
            .toList();
    }

    private ModelResponse mapToResponse(ProviderModelEntity entity) {
        return mapToResponseWithoutProvider(entity)
            .withProvider(providerService.findByProviderId(entity.providerId()));
    }

    private ModelResponse mapToResponseWithoutProvider(ProviderModelEntity entity) {
        return ModelResponse.builder()
            .modelId(entity.providerModelId())
            .enabled(entity.enabled())
            .name(entity.name())
            .features(entity.features().stream().map(ModelFeature::valueOf).toList())
            .status(ModelStatus.valueOf(entity.status()))
            .contextLength(entity.contextLength())
            .defaultParams(entity.defaultParams())
            .maxOutputTokens(entity.maxOutputTokens())
            .createdAt(entity.createdAt())
            .updatedAt(entity.updatedAt())
            .build();
    }

    public ModelResponse createModel(@Valid CreateModelRequest createRequest) {
        if (modelRepository.existsByTenantIdAndName(SecurityContextUtils.getTenantId(), createRequest.name())) {
            throw new BizException(ErrorCode.ADM_CONFLICT, createRequest.name());
        }
        ProviderModelEntity modelEntity = ProviderModelEntity.builder()
            .providerId(createRequest.providerId())
            .providerModelId(UUID.randomUUID())
            .name(createRequest.name())
            .features(createRequest.features().stream().map(Enum::name).collect(Collectors.toList()))
            .enabled(createRequest.enabled())
            .contextLength(0)
            .maxOutputTokens(0)
            .status(ModelStatus.PENDING.name())
            .ownerId(SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class).userId())
            .tenantId(SecurityContextUtils.getTenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        modelRepository.save(modelEntity);
        return mapToResponse(modelEntity);
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        modelRepository.deleteByTenantIdAndProviderModelIdIn(SecurityContextUtils.getTenantId(), ids);
    }

    public ModelResponse patchModel(UUID modelId,
        @Valid PatchModelRequest patchRequest) {
        ProviderModelEntity modelEntity = modelRepository.findOneByTenantIdAndProviderModelId(
            SecurityContextUtils.getTenantId(), modelId);
        if (patchRequest.enabled() != null) {
            modelEntity = modelEntity.withEnabled(patchRequest.enabled());
        }
        if (patchRequest.name() != null) {
            boolean exists = modelRepository.existsByTenantIdAndIdNotAndName(SecurityContextUtils.getTenantId(),
                modelEntity.id(), patchRequest.name());
            if (exists) {
                throw new BizException(ErrorCode.ADM_CONFLICT, patchRequest.name());
            }
            modelEntity = modelEntity.withName(patchRequest.name());
        }
        if (patchRequest.features() != null) {
            modelEntity = modelEntity.withFeatures(patchRequest.features().stream().map(Enum::name).toList());
        }
        modelRepository.save(modelEntity);
        return mapToResponse(modelEntity);
    }

    public boolean hasPermissions(List<UUID> ids) {
        return modelRepository.countByTenantIdAndProviderModelIdIn(SecurityContextUtils.getTenantId(), ids).equals(ids.size());
    }
}
