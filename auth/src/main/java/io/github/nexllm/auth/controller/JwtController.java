package io.github.nexllm.auth.controller;

import io.github.nexllm.auth.config.AuthProperties;
import io.github.nexllm.auth.util.JwtUtils;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/.well-known")
@RequiredArgsConstructor
public class JwtController {

    private final AuthProperties authProperties;

    @GetMapping("jwks.json")
    public Map<String, Object> publicKey() {
        String kid = "nexllm-key-1";
        String pem = authProperties.jwt().publicKey();
        RSAPublicKey rsaPublicKey = JwtUtils.parsePublicKey(pem);
        String n = JwtUtils.toBase64Url(rsaPublicKey.getModulus());
        String e = JwtUtils.toBase64Url(rsaPublicKey.getPublicExponent());
        Map<String, Object> jwk = Map.of(
            "kty", "RSA",
            "use", "sig",
            "alg", "RS256",
            "kid", kid,
            "n", n,
            "e", e
        );
        return Map.of("keys", List.of(jwk));
    }
}
