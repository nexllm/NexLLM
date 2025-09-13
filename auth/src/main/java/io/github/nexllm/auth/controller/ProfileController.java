package io.github.nexllm.auth.controller;

import io.github.nexllm.security.model.AuthUser;
import io.github.nexllm.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class ProfileController {

    @GetMapping("profile")
    public AuthUser profile() {
        return SecurityUtils.getCurrentUser(AuthUser.class);
    }
}
