package io.github.nexllm.gateway.model;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record ModelMeta(
    Model model,
    Provider provider,
    ProviderKey providerKey,
    VirtualModel virtualModel
) {

    public record VirtualModel(
        UUID virtualModelId,
        String name) {

    }

    public record Model(
        UUID providerId,
        UUID modelId,
        String name) {

    }

    public record Provider(
        UUID providerId,
        String baseUrl,
        String providerType) {

    }

    public record ProviderKey(
        UUID providerKeyId,
        Map<String, String> credential) {

        public String getApiKey() {
            return credential.get("api_key");
        }
    }

}
