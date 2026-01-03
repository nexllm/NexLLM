package io.github.nexllm.security.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemUtils {

    public static RSAPublicKey readPublicKey(String pemFilePath) {
        try {
            String pem = Files.readString(Path.of(pemFilePath));
            return parsePublicKey(pem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey readPrivateKey(String pemFilePath) {
        try {
            String pem = Files.readString(Path.of(pemFilePath));
            return parsePrivateKey(pem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static RSAPublicKey parsePublicKey(String key) throws Exception {
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(keySpec);
    }

    private static RSAPrivateKey parsePrivateKey(String pem) {
        try {
            String privateKeyPEM = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RSA Public Key", e);
        }
    }
}

