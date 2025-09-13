package io.github.nexllm.common.crypto.impl;

import io.github.nexllm.common.crypto.CryptoProvider;

public class NoOpCryptoProvider implements CryptoProvider {

    @Override
    public String encrypt(String data) {
        return data;
    }

    @Override
    public String decrypt(String data) {
        return data;
    }
}
