package io.github.nexllm.console.controller;

import io.github.nexllm.security.principal.ConsoleUserPrincipal;
import io.github.nexllm.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("me")
    public ConsoleUserPrincipal me() {
        return SecurityContextUtils.getCurrentUser(ConsoleUserPrincipal.class);
    }
}
