package io.github.nexllm.console.exception;

import io.github.nexllm.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;

public final class ConsoleHttpErrorMapper {

    public static HttpStatus map(final ErrorCode errorCode) {
        switch (errorCode) {
            case CM_INVALID_PARAM:
            case CM_MISSING_PARAM:
            case ADM_PROVIDER_ASSOCIATED_MODEL:
            case ADM_PROVIDER_ASSOCIATED_KEY:
                return HttpStatus.BAD_REQUEST;
            case CM_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case AU_INVALID_TOKEN:
            case AU_INVALID_CREDENTIALS:
            case AU_UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case AU_FORBIDDEN:
                return HttpStatus.FORBIDDEN;
            case AU_DUPLICATE_USER:
            case ADM_CONFLICT:
                return HttpStatus.CONFLICT;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
