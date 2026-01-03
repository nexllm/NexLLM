package io.github.nexllm.console.service;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.entity.ProviderEntity;
import io.github.nexllm.common.entity.ProviderModelEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.console.model.request.CreateProviderRequest;
import io.github.nexllm.console.model.response.ProviderResponse;
import io.github.nexllm.console.model.response.TreeNode;
import io.github.nexllm.console.repository.ModelRepository;
import io.github.nexllm.console.repository.ProviderKeyRepository;
import io.github.nexllm.console.repository.ProviderRepository;
import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import io.github.nexllm.security.util.SecurityContextUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ModelRepository modelRepository;
    private final ProviderKeyRepository providerKeyRepository;
    private final ProviderRepository providerRepository;

    public List<ProviderResponse> getCurrentTenantProviders() {
        return providerRepository.findByTenantIdOrderByIdDesc(SecurityContextUtils.getTenantId())
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public ProviderResponse findByProviderId(UUID providerId) {
        ProviderEntity entity = providerRepository.findByProviderId(providerId);
        return mapToResponse(entity);
    }

    private ProviderResponse mapToResponse(ProviderEntity entity) {
        return ProviderResponse.builder()
            .name(entity.name())
            .baseUrl(entity.baseUrl())
            .providerId(entity.providerId())
            .extraConfig(entity.extraConfig())
            .providerType(entity.providerType())
            .system(entity.system())
            .description(entity.description())
            .enabled(entity.enabled())
            .modelCount(modelRepository.countByProviderId(entity.providerId()))
            .createdAt(entity.createdAt())
            .updatedAt(entity.updatedAt())
            .build()
            ;
    }

    public List<TreeNode> getProviderTree() {
        Iterable<ProviderEntity> providers = providerRepository.findAll();
        List<ProviderModelEntity> models = modelRepository.findAll();

        List<TreeNode> treeNodes = new ArrayList<>();
        providers.forEach(provider -> {
            List<ProviderModelEntity> providerModels = models.stream()
                .filter(it -> it.providerId().equals(provider.providerId()))
                .toList();
            treeNodes.add(TreeNode.builder()
                .title(provider.name())
                .value(provider.providerId().toString())
                .key(provider.providerId().toString())
                .children(buildModelTree(providerModels))
                .selectable(false)
                .build());
        });
        return treeNodes.stream().filter(it -> !it.children().isEmpty()).toList();
    }

    private List<TreeNode> buildModelTree(List<ProviderModelEntity> models) {
        return models.stream()
            .map(model -> TreeNode.builder()
                .title(model.name())
                .value(model.providerModelId().toString())
                .key(model.providerModelId().toString())
                .selectable(true)
                .build()).toList();

    }

    public ProviderResponse createProvider(@Valid CreateProviderRequest createProviderRequest) {
        if (providerRepository.existsByNameAndTenantId(createProviderRequest.name(),
            SecurityContextUtils.getTenantId())) {
            throw new BizException(ErrorCode.ADM_CONFLICT, createProviderRequest.name());
        }
        ProviderEntity providerEntity = ProviderEntity.builder()
            .providerId(UUID.randomUUID())
            .name(createProviderRequest.name())
            .baseUrl(createProviderRequest.baseUrl())
            .providerType(createProviderRequest.providerType())
            .extraConfig(createProviderRequest.extraConfig())
            .description(createProviderRequest.description())
            .system(false)
            .enabled(true)
            .ownerId(SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class).userId())
            .tenantId(SecurityContextUtils.getTenantId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        providerRepository.save(providerEntity);
        return mapToResponse(providerEntity);
    }

    public boolean hasPermission(@NotNull UUID id) {
        return hasPermissions(List.of(id));
    }

    public boolean hasPermissions(@NotNull List<UUID> ids) {
        List<ProviderEntity> results = providerRepository.findAllByTenantIdAndProviderIdIn(
            SecurityContextUtils.getTenantId(),
            ids);
        return results.size() == ids.size();
    }

    @Transactional
    public void batchDelete(@NotNull List<UUID> ids) {
        Integer usedModelCount = modelRepository.countByTenantIdAndProviderIdIn(SecurityContextUtils.getTenantId(), ids);
        if (usedModelCount > 0) {
            throw new BizException(ErrorCode.ADM_PROVIDER_ASSOCIATED_MODEL);
        }
        Integer usedProviderKeyCount = providerKeyRepository.countByTenantIdAndProviderIdIn(SecurityContextUtils.getTenantId(),
            ids);
        if (usedProviderKeyCount > 0) {
            throw new BizException(ErrorCode.ADM_PROVIDER_ASSOCIATED_KEY);
        }
        providerRepository.deleteByProviderIdIn(ids);
    }

    public ProviderResponse getProvider(UUID providerId) {
        ProviderEntity provider = providerRepository.findOneByTenantIdAndProviderIdOrSystem(
            SecurityContextUtils.getTenantId(), providerId, true);
        return mapToResponse(provider);
    }
}
