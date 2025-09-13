package io.github.nexllm.auth.controller;

import io.github.nexllm.auth.model.request.LoginRequest;
import io.github.nexllm.auth.model.request.RegisterRequest;
import io.github.nexllm.auth.model.response.LoginResponse;
import io.github.nexllm.auth.model.response.RegisterResponse;
import io.github.nexllm.auth.service.AuthService;
import io.github.nexllm.security.SecurityConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        ResponseCookie accessTokenCookie = ResponseCookie.from(SecurityConstants.ACCESS_TOKEN,
                loginResponse.accessToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(SecurityConstants.ACCESS_TOKEN_DURATION)
            .sameSite("Strict")
            .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(SecurityConstants.REFRESH_TOKEN,
                loginResponse.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(SecurityConstants.REFRESH_TOKEN_DURATION)
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(loginResponse);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie accessTokenCookie = ResponseCookie.from(SecurityConstants.ACCESS_TOKEN, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(SecurityConstants.REFRESH_TOKEN, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .build();
    }
}
