package io.github.nexllm.security.util;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.security.model.AuthenticatedUser;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

public final class ReactiveSecurityUtils {

    @SuppressWarnings("unchecked")
    public static <T extends AuthenticatedUser> Mono<T> getCurrentUser(Class<T> clazz) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(context -> {
                Authentication authentication = context.getAuthentication();
                if (authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();
                    if (clazz.isInstance(principal)) {
                        return Mono.just((T) principal);
                    }
                }
                return Mono.error(new BizException(ErrorCode.AU_UNAUTHORIZED));
            });
    }

    public static Mono<UUID> getTenantId() {
        return getCurrentUser(AuthenticatedUser.class).map(AuthenticatedUser::tenantId);
    }
}
