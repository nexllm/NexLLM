package io.github.nexllm.security.util;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.security.principal.AuthenticatedUserPrincipal;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContextUtils {

    @SuppressWarnings("unchecked")
    public static <T extends AuthenticatedUserPrincipal> T getCurrentUser(Class<T> clazz) {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (clazz.isInstance(principal)) {
                return (T) principal;
            }
        }
        throw new BizException(ErrorCode.AU_UNAUTHORIZED);
    }

    public static UUID getTenantId() {
        return getCurrentUser(AuthenticatedUserPrincipal.class).tenantId();
    }
}
