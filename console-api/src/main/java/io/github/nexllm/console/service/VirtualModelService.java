package io.github.nexllm.console.service;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.entity.VirtualModelEntity;
import io.github.nexllm.common.entity.VirtualModelMappingEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.console.model.request.CreateVirtualModelRequest;
import io.github.nexllm.console.model.request.PatchVirtualModelRequest;
import io.github.nexllm.console.model.response.ModelResponse;
import io.github.nexllm.console.model.response.VirtualModelResponse;
import io.github.nexllm.console.repository.VirtualModelMappingRepository;
import io.github.nexllm.console.repository.VirtualModelRepository;
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
public class VirtualModelService {

    private final ModelService modelService;
    private final VirtualModelRepository virtualModelRepository;
    private final VirtualModelMappingRepository virtualModelMappingRepository;

    public List<VirtualModelResponse> getModels() {
        return virtualModelRepository.findAll(Sort.by(Order.desc("id"))).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public VirtualModelResponse createModel(@Valid CreateVirtualModelRequest createVirtualModelRequest) {
        String name = createVirtualModelRequest.name();
        if (!createVirtualModelRequest.name().startsWith("vm:")) {
            name = "vm:" + name;
        }
        if (virtualModelRepository.existsByTenantIdAndName(SecurityContextUtils.getTenantId(), name)) {
            throw new BizException(ErrorCode.ADM_CONFLICT, createVirtualModelRequest.name());
        }
        UUID virtualModelId = UUID.randomUUID();
        VirtualModelEntity virtualModelEntity = VirtualModelEntity.builder()
            .name(name)
            .description(createVirtualModelRequest.description())
            .virtualModelId(virtualModelId)
            .enabled(true)
            .userId(SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class).userId())
            .tenantId(SecurityContextUtils.getTenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        virtualModelEntity = virtualModelRepository.save(virtualModelEntity);
        List<VirtualModelMappingEntity> mappings = createVirtualModelRequest.modelIds().stream()
            .map(modelId -> VirtualModelMappingEntity.builder()
                .modelId(modelId)
                .virtualModelId(virtualModelId)
                .userId(SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class).userId())
                .tenantId(SecurityContextUtils.getTenantId())
                .weight(0)
                .priority(0)
                .build())
            .toList();
        virtualModelMappingRepository.saveAll(mappings);
        return mapToResponse(virtualModelEntity);
    }

    private VirtualModelResponse mapToResponse(VirtualModelEntity entity) {
        List<VirtualModelResponse.MappedModelResponse> mappings = virtualModelMappingRepository.findByVirtualModelId(
                entity.virtualModelId())
            .stream().map(mapping -> {
                ModelResponse model = modelService.findByModelId(mapping.modelId());
                return VirtualModelResponse.MappedModelResponse
                    .builder()
                    .model(model)
                    .weight(mapping.weight())
                    .priority(mapping.priority())
                    .build();
            }).toList();
        return VirtualModelResponse
            .builder()
            .name(entity.name())
            .virtualModelId(entity.virtualModelId())
            .enabled(entity.enabled())
            .models(mappings)
            .description(entity.description())
            .createdAt(entity.createdAt())
            .updatedAt(entity.updatedAt())
            .build();
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        virtualModelRepository.deleteByTenantIdAndVirtualModelIdIn(SecurityContextUtils.getTenantId(), ids);
    }

    @Transactional
    public VirtualModelResponse patchVirtualModel(UUID virtualModelId,
        @Valid PatchVirtualModelRequest patchVirtualModelRequest) {
        VirtualModelEntity entity = virtualModelRepository.findAllByTenantIdAndVirtualModelId(
            SecurityContextUtils.getTenantId(), virtualModelId);
        if (patchVirtualModelRequest.name() != null) {
            entity = entity.withName(patchVirtualModelRequest.name());
        }
        if (patchVirtualModelRequest.description() != null) {
            entity = entity.withDescription(patchVirtualModelRequest.description());
        }
        if (patchVirtualModelRequest.modelIds() != null) {
            boolean hasPermissions = modelService.hasPermissions(patchVirtualModelRequest.modelIds());
            if (!hasPermissions) {
                throw new BizException(ErrorCode.AU_FORBIDDEN);
            }
            virtualModelMappingRepository.deleteByModelId(virtualModelId);
            List<VirtualModelMappingEntity> mappings = patchVirtualModelRequest.modelIds().stream()
                .map(modelId -> VirtualModelMappingEntity.builder()
                    .modelId(modelId)
                    .virtualModelId(virtualModelId)
                    .userId(SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class).userId())
                    .tenantId(SecurityContextUtils.getTenantId())
                    .weight(0)
                    .priority(0)
                    .build())
                .toList();
            virtualModelMappingRepository.saveAll(mappings);
        }
        virtualModelRepository.save(entity);
        return mapToResponse(entity);
    }
}
