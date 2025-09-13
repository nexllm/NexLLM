package io.github.nexllm.common.crypto;

public interface CryptoProvider {
    String encrypt(String data);
    String decrypt(String data);
}
