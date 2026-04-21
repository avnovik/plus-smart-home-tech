package ru.yandex.practicum.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.dto.error.ProductNotFoundException;

/**
 * Обработчик ошибок shopping-store, приводящий ответы к контракту OpenAPI.
 */
@RestControllerAdvice
@Slf4j
public class ShoppingStoreExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProductNotFoundException> handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
            throw ex;
        }

        log.warn("{}: {}", ex.getStatusCode(), ex.getReason());

        ProductNotFoundException body = new ProductNotFoundException(
                null,
                null,
                "404 NOT_FOUND",
                "Товар не найден",
                ex.getReason(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
