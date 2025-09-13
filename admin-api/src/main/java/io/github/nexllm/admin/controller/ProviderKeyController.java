package io.github.nexllm.admin.controller;

import io.github.nexllm.admin.model.request.CreateProviderKeyRequest;
import io.github.nexllm.admin.model.request.PatchProviderKeyRequest;
import io.github.nexllm.admin.model.response.ProviderKeyResponse;
import io.github.nexllm.admin.model.response.ProviderKeyValueResponse;
import io.github.nexllm.admin.service.ProviderKeyService;
import io.github.nexllm.common.model.request.BatchDeleteRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/provider/keys")
@RequiredArgsConstructor
public class ProviderKeyController {

    private final ProviderKeyService providerKeyService;

    @GetMapping
    public List<ProviderKeyResponse> getProviderKeys(@RequestParam(required = false) UUID providerId) {
        return providerKeyService.getProviderKeys(providerId);
    }

    @PostMapping
    @PreAuthorize("@providerService.hasPermission(#createRequest.providerId())")
    public ProviderKeyResponse createProviderKey(@RequestBody @Valid CreateProviderKeyRequest createRequest) {
        return providerKeyService.createProviderKey(createRequest);
    }

    @GetMapping("{providerKeyId}/reveal")
    public ProviderKeyValueResponse getProviderKeyValue(@PathVariable("providerKeyId") UUID providerKeyId) {
        return providerKeyService.getProviderKeyValue(providerKeyId);
    }

    @PatchMapping("{providerKeyId}")
    public ProviderKeyResponse patchProviderKey(
        @PathVariable("providerKeyId") UUID providerKeyId,
        @RequestBody @Valid PatchProviderKeyRequest patchRequest) {
        return providerKeyService.patchProviderKey(providerKeyId, patchRequest);
    }

    @DeleteMapping
    public void batchDeleteProviderKey(@RequestBody @Valid BatchDeleteRequest batchDeleteRequest) {
        providerKeyService.batchDelete(batchDeleteRequest.ids());
    }
}
