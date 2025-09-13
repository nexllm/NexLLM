package io.github.nexllm.admin.controller;

import io.github.nexllm.admin.model.request.CreateProviderRequest;
import io.github.nexllm.admin.model.response.ModelResponse;
import io.github.nexllm.admin.model.response.ProviderResponse;
import io.github.nexllm.admin.model.response.TreeNode;
import io.github.nexllm.admin.service.ModelService;
import io.github.nexllm.admin.service.ProviderService;
import io.github.nexllm.common.model.request.BatchDeleteRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    private final ModelService modelService;

    @PostMapping
    public ProviderResponse createProvider(@RequestBody @Valid CreateProviderRequest createProviderRequest) {
        return providerService.createProvider(createProviderRequest);
    }

    @GetMapping
    public List<ProviderResponse> getProviders() {
        return providerService.getCurrentTenantProviders();
    }

    @GetMapping("tree")
    public List<TreeNode> getProviderTree() {
        return providerService.getProviderTree();
    }

    @GetMapping("{providerId}")
    public ProviderResponse getProvider(@PathVariable("providerId") UUID providerId) {
        return providerService.getProvider(providerId);
    }

    @Operation(operationId = "batchDeleteProvider")
    @DeleteMapping
    @PreAuthorize("@providerService.hasPermissions(#batchDeleteRequest.ids())")
    public void batchDelete(@RequestBody @Valid BatchDeleteRequest batchDeleteRequest) {
        providerService.batchDelete(batchDeleteRequest.ids());
    }

    @GetMapping("{providerId}/models")
    public List<ModelResponse> getProviderModels(@PathVariable("providerId") UUID providerId) {
        return modelService.getProviderModels(providerId);
    }
}
