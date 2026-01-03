package io.github.nexllm.gateway.exception;

import io.github.nexllm.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;

public final class GatewayHttpErrorMapper {

    public static HttpStatus map(final ErrorCode errorCode) {
        switch (errorCode) {
            case CM_INVALID_PARAM:
            case CM_MISSING_PARAM:
            //gateway
            case CN_UNSUPPORTED_MODEL:
            case CN_VMODEL_NOT_BIND:
                return HttpStatus.BAD_REQUEST;
            case CM_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case AU_INVALID_TOKEN:
            case AU_INVALID_CREDENTIALS:
            case AU_UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case GW_TOO_MANY_REQUESTS:
                return HttpStatus.TOO_MANY_REQUESTS;
            case AU_FORBIDDEN:
                return HttpStatus.FORBIDDEN;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
