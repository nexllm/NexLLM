package io.github.nexllm.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import io.github.nexllm.security.SecurityConstants;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public final class JwtUtils {

    private final RSAKeyProvider keyProvider;

    public JwtUtils(RSAKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public String sign(JwtClaims claims, Duration expiration) {
        Algorithm algorithm = Algorithm.RSA256(null, keyProvider.getPrivateKey());
        Builder builder = JWT.create()
            .withIssuer("NexLLM-Identity")
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(expiration))
            .withSubject(claims.userId().toString())
            .withClaim(SecurityConstants.CLAIM_USER_ID, claims.userId().toString())
            .withClaim(SecurityConstants.CLAIM_TENANT_ID, claims.tenantId().toString())
            .withClaim(SecurityConstants.CLAIM_USERNAME, claims.username())
            .withClaim(SecurityConstants.CLAIM_ROLES, claims.roles())
            .withClaim(SecurityConstants.CLAIM_SOURCE, claims.source().name());
        return builder.sign(algorithm);
    }

    public JwtClaims verify(String token) {
        String kid = keyProvider.getPrivateKeyId();
        Algorithm algorithm = Algorithm.RSA256( keyProvider.getPublicKeyById(kid));
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT =  verifier.verify(token);
        return getJwtClaims(decodedJWT);
    }

    private JwtClaims getJwtClaims(DecodedJWT decodedJWT) {
        return JwtClaims.builder()
            .userId(UUID.fromString(decodedJWT.getClaim(SecurityConstants.CLAIM_USER_ID).asString()))
            .tenantId(UUID.fromString(decodedJWT.getClaim(SecurityConstants.CLAIM_TENANT_ID).asString()))
            .source(PrincipalSource.valueOf(decodedJWT.getClaim(SecurityConstants.CLAIM_USERNAME).asString()))
            .roles(decodedJWT.getClaim(SecurityConstants.CLAIM_ROLES).asList(String.class))
            .source(PrincipalSource.valueOf(decodedJWT.getClaim(SecurityConstants.CLAIM_SOURCE).asString()))
            .build();
    }

    public JwtClaims decode(String token) {
        DecodedJWT decodedJWT =  JWT.decode(token);
        return getJwtClaims(decodedJWT);
    }

    public static String toBase64Url(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0) {
            // remove leading zero byte
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
