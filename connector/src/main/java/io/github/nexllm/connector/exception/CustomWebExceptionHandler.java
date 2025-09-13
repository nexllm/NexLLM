package io.github.nexllm.connector.exception;

import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.exception.BizException;
import io.github.nexllm.common.exception.LLMError;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.common.util.MessageUtils;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
public class CustomWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message;
        if (ex instanceof BizException e) {
            status = e.getErrorCode().getHttpStatus();
            message = MessageUtils.resolveMessage(exchange.getLocaleContext().getLocale(), e.getErrorCode(), e.getArgs());
            log.info(message);
        } else if (ex instanceof ResponseStatusException e) {
            status = HttpStatus.valueOf(e.getStatusCode().value());
            message = e.getReason();
            log.info(message);
        } else {
            message = ex.getMessage();
            log.error(ex.getMessage(), ex);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        LLMError errorResponse = new LLMError(LLMError.ErrorDetails.builder()
            .code(ErrorCode.CM_SYSTEM_ERROR.name())
            .message(message)
            .build());

        String errors = JsonUtils.toJsonWithoutNull(errorResponse);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
            .bufferFactory().wrap(errors.getBytes(StandardCharsets.UTF_8))));
    }
}
