package io.github.nexllm.connector.exception;

import io.github.nexllm.common.exception.LLMError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LlmException extends RuntimeException {
    private final LLMError error;
    private final HttpStatus status;

    public LlmException(LLMError error, HttpStatus status) {
        super(error.error().message());
        this.error = error;
        this.status = status;
    }
}
