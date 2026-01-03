package io.github.nexllm.security.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface KeyProvider {

    RSAPrivateKey getPrivateKey(String kid);

    RSAPublicKey getPublicKey(String kid);

    String getDefaultKid();
}

