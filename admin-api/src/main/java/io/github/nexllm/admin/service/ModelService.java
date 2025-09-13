package io.github.nexllm.admin.service;

import io.github.nexllm.admin.model.request.CreateModelRequest;
import io.github.nexllm.admin.model.request.PatchModelRequest;
import io.github.nexllm.admin.model.response.ModelResponse;
import io.github.nexllm.admin.repository.ModelRepository;
import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.constants.ModelFeature;
import io.github.nexllm.common.constants.ModelStatus;
import io.github.nexllm.common.entity.LlmModelEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.security.model.AuthUser;
import io.github.nexllm.security.util.SecurityUtils;
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
        return modelRepository.findByTenantId(SecurityUtils.getTenantId(), Sort.by(Order.desc("id")))
            .stream()
            .filter(it -> providerId == null || it.providerId().equals(providerId))
            .filter(it -> status == null || it.status().equals(status))
            .map(this::mapToResponse)
            .toList();
    }

    public ModelResponse findByModelId(UUID modelId) {
        LlmModelEntity modelEntity = modelRepository.findByModelId(modelId);
        return mapToResponse(modelEntity);
    }

    public List<ModelResponse> getProviderModels(UUID providerId) {
        return modelRepository.findByProviderId(providerId)
            .stream()
            .map(this::mapToResponseWithoutProvider)
            .toList();
    }

    private ModelResponse mapToResponse(LlmModelEntity entity) {
        return mapToResponseWithoutProvider(entity)
            .withProvider(providerService.findByProviderId(entity.providerId()));
    }

    private ModelResponse mapToResponseWithoutProvider(LlmModelEntity entity) {
        return ModelResponse.builder()
            .modelId(entity.modelId())
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
        if (modelRepository.existsByTenantIdAndName(SecurityUtils.getTenantId(), createRequest.name())) {
            throw new BizException(ErrorCode.ADM_CONFLICT, createRequest.name());
        }
        LlmModelEntity modelEntity = LlmModelEntity.builder()
            .modelId(UUID.randomUUID())
            .providerId(createRequest.providerId())
            .name(createRequest.name())
            .features(createRequest.features().stream().map(Enum::name).collect(Collectors.toList()))
            .enabled(createRequest.enabled())
            .contextLength(0)
            .maxOutputTokens(0)
            .status(ModelStatus.PENDING.name())
            .userId(SecurityUtils.getCurrentUser(AuthUser.class).userId())
            .tenantId(SecurityUtils.getTenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        modelRepository.save(modelEntity);
        return mapToResponse(modelEntity);
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        modelRepository.deleteByTenantIdAndModelIdIn(SecurityUtils.getTenantId(), ids);
    }

    public ModelResponse patchModel(UUID modelId,
        @Valid PatchModelRequest patchRequest) {
        LlmModelEntity modelEntity = modelRepository.findOneByTenantIdAndModelId(
            SecurityUtils.getTenantId(), modelId);
        if (patchRequest.enabled() != null) {
            modelEntity = modelEntity.withEnabled(patchRequest.enabled());
        }
        if (patchRequest.name() != null) {
            boolean exists = modelRepository.existsByTenantIdAndIdNotAndName(SecurityUtils.getTenantId(),
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
        return modelRepository.countByTenantIdAndModelIdIn(SecurityUtils.getTenantId(), ids).equals(ids.size());
    }
}
