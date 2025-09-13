package io.github.nexllm.common.exception;

import io.github.nexllm.common.constants.ErrorCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public BizException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }
}
