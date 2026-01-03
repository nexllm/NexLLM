package io.github.nexllm.security.jwt;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class VerifyOnlyKeyProvider implements RSAKeyProvider {

    private final RSAPublicKey publicKey;
    private final String kid;

    public VerifyOnlyKeyProvider(String publicKey) throws Exception {
        this("default", publicKey);
    }


    public VerifyOnlyKeyProvider(String kid, String publicKey) throws Exception {
        this.kid = kid;
        this.publicKey = loadPublicKey(publicKey);
    }

    private RSAPublicKey loadPublicKey(String path) throws Exception {
        String pem = Files.readString(Path.of(path))
            .replaceAll("-----\\w+ PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public String getPrivateKeyId() {
        return kid;
    }
}

