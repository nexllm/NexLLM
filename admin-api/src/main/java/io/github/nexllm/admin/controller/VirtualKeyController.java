package io.github.nexllm.admin.controller;

import io.github.nexllm.admin.model.request.CreateVirtualKeyRequest;
import io.github.nexllm.admin.model.response.VirtualKeyResponse;
import io.github.nexllm.admin.service.VirtualKeyService;
import io.github.nexllm.common.model.request.BatchDeleteRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/virtual-keys")
@RequiredArgsConstructor
public class VirtualKeyController {

    private final VirtualKeyService virtualKeyService;

    @GetMapping
    public List<VirtualKeyResponse> getVirtualKeys() {
        return virtualKeyService.getKeys();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VirtualKeyResponse createVirtualKey(
        @Valid @RequestBody CreateVirtualKeyRequest createVirtualKeyRequest) {
        return virtualKeyService.createKey(createVirtualKeyRequest);
    }

    @DeleteMapping
    public void batchDeleteVirtualKey(@RequestBody BatchDeleteRequest batchDeleteRequest) {
        virtualKeyService.batchDelete(batchDeleteRequest.ids());
    }
}
