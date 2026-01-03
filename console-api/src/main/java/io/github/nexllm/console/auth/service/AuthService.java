package io.github.nexllm.console.auth.service;


import static io.github.nexllm.security.SecurityConstants.ACCESS_TOKEN_DURATION;
import static io.github.nexllm.security.SecurityConstants.REFRESH_TOKEN_DURATION;

import io.github.nexllm.common.constants.EntityStatus;
import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.entity.TenantEntity;
import io.github.nexllm.common.entity.UserEntity;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.common.util.BeanMapperUtils;
import io.github.nexllm.console.auth.config.AuthProperties;
import io.github.nexllm.console.auth.model.LoginRequest;
import io.github.nexllm.console.auth.model.LoginResponse;
import io.github.nexllm.console.auth.model.RegisterRequest;
import io.github.nexllm.console.model.response.RegisterResponse;
import io.github.nexllm.console.repository.TenantRepository;
import io.github.nexllm.console.repository.UserRepository;
import io.github.nexllm.infra.redis.accessor.RedisAccessor;
import io.github.nexllm.infra.redis.key.RedisKeys;
import io.github.nexllm.infra.redis.value.UserProfile;
import io.github.nexllm.security.SecurityConstants;
import io.github.nexllm.security.jwt.JwtClaims;
import io.github.nexllm.security.jwt.JwtUtils;
import io.github.nexllm.security.jwt.PrincipalSource;
import io.github.nexllm.security.jwt.StaticKeyProvider;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisAccessor redisAccessor;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository repository,
        TenantRepository tenantRepository,
        PasswordEncoder passwordEncoder, RedisAccessor redisAccessor,
        AuthProperties authProperties) throws Exception {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisAccessor = redisAccessor;
        this.jwtUtils = new JwtUtils(
            new StaticKeyProvider(authProperties.jwt().publicKey(), authProperties.jwt().privateKey()));
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity userEntity = repository.findOneByUsername(request.username());
        if (userEntity == null ||
            !passwordEncoder.matches(request.password(), userEntity.password())) {
            throw new BizException(ErrorCode.AU_INVALID_CREDENTIALS);
        }
        JwtClaims principal = JwtClaims.builder()
            .tenantId(userEntity.tenantId())
            .username(userEntity.username())
            .userId(userEntity.userId())
            .roles(userEntity.roles())
            .source(PrincipalSource.CONSOLE)
            .build();
        String accessToken = jwtUtils.sign(principal, ACCESS_TOKEN_DURATION);
        String refreshToken = jwtUtils.sign(principal, REFRESH_TOKEN_DURATION);
        return new LoginResponse(accessToken, refreshToken, ACCESS_TOKEN_DURATION.getSeconds(), "Bearer");
    }

    public RegisterResponse register(RegisterRequest request) {
        UserEntity userEntity = repository.findOneByUsername(request.username());
        if (userEntity != null) {
            throw new BizException(ErrorCode.AU_DUPLICATE_USER, request.username());
        }
        UUID userId = UUID.randomUUID();
        TenantEntity tenantEntity = TenantEntity.builder()
            .tenantId(UUID.randomUUID())
            .name(request.username())
            .ownerUserId(userId)
            .status(EntityStatus.ACTIVE.getCode())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        tenantRepository.save(tenantEntity);
        UserEntity user = UserEntity.builder()
            .userId(userId)
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .status(EntityStatus.ACTIVE.getCode())
            .tenantId(tenantEntity.tenantId())
            .roles(List.of(SecurityConstants.ROLE_ADMIN))
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
        UserEntity saved = repository.save(user);
        redisAccessor.set(RedisKeys.USER_PROFILE, new UserProfile(saved.tenantId(), saved.userId()));
        return BeanMapperUtils.map(saved, RegisterResponse.class);
    }
}
