package io.github.nexllm.connector.router;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.constants.FieldI18nKey;
import io.github.nexllm.common.crypto.CryptoProvider;
import io.github.nexllm.common.entity.LlmModelEntity;
import io.github.nexllm.common.entity.LlmProviderEntity;
import io.github.nexllm.common.entity.LlmProviderKeyEntity;
import io.github.nexllm.common.entity.VirtualModelEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.connector.model.LlmModel;
import io.github.nexllm.connector.model.LlmProvider;
import io.github.nexllm.connector.model.LlmProviderKey;
import io.github.nexllm.connector.model.ModelMeta;
import io.github.nexllm.connector.model.VirtualModel;
import io.github.nexllm.connector.repository.LlmProviderKeyRepository;
import io.github.nexllm.connector.repository.LlmProviderRepository;
import io.github.nexllm.connector.repository.VirtualModelRepository;
import io.github.nexllm.security.util.ReactiveSecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ModelRouter {

    private final ModelRoutingStrategy modelRoutingStrategy;
    private final CryptoProvider cryptoProvider;
    private final LlmProviderRepository providerRepository;
    private final LlmProviderKeyRepository providerKeyRepository;
    private final VirtualModelRepository virtualModelRepository;

    public Mono<ModelMeta> resolveMeta(String vmName) {
        return ReactiveSecurityUtils.getTenantId()
            .flatMap(tenantId -> virtualModelRepository.findByName(tenantId, vmName)
                .switchIfEmpty(Mono.error(new BizException(ErrorCode.CM_NOT_FOUND, FieldI18nKey.V_MODEL, vmName)))
                .flatMap(virtualModelEntity -> virtualModelRepository.findModelsByVirtualModelId(tenantId,
                        virtualModelEntity.virtualModelId())
                    .map(this::toModels)
                    .switchIfEmpty(Mono.error(new BizException(ErrorCode.CN_VMODEL_NOT_BIND)))
                    .flatMap(models -> selectModel(tenantId, models))
                    .flatMap(model -> providerRepository.findByProviderId(model.providerId())
                        .map(this::toProvider)
                        .map(provider -> toMeta(virtualModelEntity, model, provider)
                        )
                    )
                ));
    }

    private LlmProvider toProvider(LlmProviderEntity provider) {
        return new LlmProvider(provider.providerId(), provider.baseUrl(), provider.sdkClientClass());
    }

    private Mono<LlmModel> selectModel(UUID tenantId, List<LlmModel> models) {
        return modelRoutingStrategy.select(tenantId, models);
    }

    private List<LlmModel> toModels(List<LlmModelEntity> models) {
        return models.stream()
            .map(m -> new LlmModel(m.providerId(), m.modelId(), m.name()))
            .toList();
    }

    private ModelMeta toMeta(VirtualModelEntity virtualModelEntity, LlmModel model, LlmProvider provider) {
        return ModelMeta
            .builder()
            .model(model)
            .provider(provider)
            .virtualModel(new VirtualModel(virtualModelEntity.virtualModelId(), virtualModelEntity.name()))
            .build();
    }

    public Mono<LlmProviderKey> selectKey(UUID providerId, UUID modelId) {
        return ReactiveSecurityUtils.getTenantId()
            .flatMap(tenantId -> providerRepository.findByProviderId(providerId)
                .flatMap(provider -> providerKeyRepository
                    .findFirstByProviderId(tenantId, provider.providerId())
                    .map(this::buildKey)
                ));
    }

    private LlmProviderKey buildKey(LlmProviderKeyEntity providerKeyEntity) {
        String decrypted = cryptoProvider.decrypt(providerKeyEntity.keyEnc());
        Map<String, String> credential = JsonUtils.toObject(decrypted, Map.class);
        return new LlmProviderKey(providerKeyEntity.providerKeyId(), credential);
    }
}
