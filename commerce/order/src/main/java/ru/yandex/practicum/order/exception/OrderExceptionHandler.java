package ru.yandex.practicum.order.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.dto.error.ErrorResponse;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class OrderExceptionHandler {

    private static final String ORDER_NOT_FOUND_REASON = "Не найден заказ";
    private static final String NO_PRODUCT_IN_WAREHOUSE_REASON = "Нет заказываемого товара на складе";

    private final OrderErrorFactory errorFactory;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String reason = ex.getReason();

        if (status == HttpStatus.UNAUTHORIZED) {
            log.warn("{}: {}", status, reason);
            ErrorResponse body = errorFactory.notAuthorized(reason);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        if (status == HttpStatus.BAD_REQUEST) {
            log.warn("{}: {}", status, reason);
            String userMessage = "Ошибка запроса";
            if (ORDER_NOT_FOUND_REASON.equals(reason) || NO_PRODUCT_IN_WAREHOUSE_REASON.equals(reason)) {
                userMessage = reason;
            }

            ErrorResponse body = errorFactory.badRequest(userMessage, reason);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        if (status == HttpStatus.NOT_FOUND) {
            log.warn("{}: {}", status, reason);
            String userMessage = "Ресурс не найден";
            if (ORDER_NOT_FOUND_REASON.equals(reason)) {
                userMessage = reason;
            }
            ErrorResponse body = errorFactory.notFound(userMessage, reason);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        throw ex;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        log.warn("400 BAD_REQUEST: {}", ex.getMessage());
        ErrorResponse body = errorFactory.badRequest("Ошибка запроса", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
