package io.github.nexllm.gateway.router;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.constants.FieldI18nKey;
import io.github.nexllm.common.crypto.CryptoProvider;
import io.github.nexllm.common.entity.ProviderEntity;
import io.github.nexllm.common.entity.ProviderKeyEntity;
import io.github.nexllm.common.entity.ProviderModelEntity;
import io.github.nexllm.common.entity.VirtualModelEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.gateway.infra.persistence.postgres.PostgresLlmProviderKeyRepository;
import io.github.nexllm.gateway.infra.persistence.postgres.PostgresLlmProviderRepository;
import io.github.nexllm.gateway.infra.persistence.postgres.PostgresVirtualModelRepository;
import io.github.nexllm.gateway.model.ModelMeta;
import io.github.nexllm.gateway.model.ModelMeta.Provider;
import io.github.nexllm.security.util.ReactiveSecurityContextUtils;
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
    private final PostgresLlmProviderRepository providerRepository;
    private final PostgresLlmProviderKeyRepository providerKeyRepository;
    private final PostgresVirtualModelRepository virtualModelRepository;

    public Mono<ModelMeta> resolveMeta(String vmName) {
        return ReactiveSecurityContextUtils.getTenantId()
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

    private ModelMeta.Provider toProvider(ProviderEntity provider) {
        return new ModelMeta.Provider(provider.providerId(), provider.baseUrl(), provider.providerType());
    }

    private Mono<ModelMeta.Model> selectModel(UUID tenantId, List<ModelMeta.Model> models) {
        return modelRoutingStrategy.select(tenantId, models);
    }

    private List<ModelMeta.Model> toModels(List<ProviderModelEntity> models) {
        return models.stream()
            .map(m -> new ModelMeta.Model(m.providerId(), m.providerModelId(), m.name()))
            .toList();
    }

    private ModelMeta toMeta(VirtualModelEntity virtualModelEntity, ModelMeta.Model model, Provider provider) {
        return ModelMeta
            .builder()
            .model(model)
            .provider(provider)
            .virtualModel(new ModelMeta.VirtualModel(virtualModelEntity.virtualModelId(), virtualModelEntity.name()))
            .build();
    }

    public Mono<ModelMeta.ProviderKey> selectKey(UUID providerId, UUID modelId) {
        return ReactiveSecurityContextUtils.getTenantId()
            .flatMap(tenantId -> providerRepository.findByProviderId(providerId)
                .flatMap(provider -> providerKeyRepository
                    .findFirstByProviderId(tenantId, provider.providerId())
                    .map(this::buildKey)
                ));
    }

    private ModelMeta.ProviderKey buildKey(ProviderKeyEntity providerKeyEntity) {
        String decrypted = cryptoProvider.decrypt(providerKeyEntity.keyEnc());
        Map<String, String> credential = JsonUtils.toObject(decrypted, Map.class);
        return new ModelMeta.ProviderKey(providerKeyEntity.providerKeyId(), credential);
    }
}
