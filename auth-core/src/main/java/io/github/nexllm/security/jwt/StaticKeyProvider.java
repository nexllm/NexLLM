package io.github.nexllm.security.jwt;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class StaticKeyProvider implements RSAKeyProvider {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String kid;

    public StaticKeyProvider(String privateKey, String publicKey) throws Exception {
        this("default", privateKey, publicKey);
    }

    public StaticKeyProvider(String kid, String privateKey, String publicKey) throws Exception {
        this.kid = kid;
        this.privateKey = loadPrivateKey(privateKey);
        this.publicKey = loadPublicKey(publicKey);
    }

    private RSAPrivateKey loadPrivateKey(String key) throws Exception {
        String pem = key
            .replaceAll("-----\\w+ PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey(String key) throws Exception {
        String pem = key
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
        return privateKey;
    }

    @Override
    public String getPrivateKeyId() {
        return kid;
    }
}

