package ru.yandex.practicum.cart.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.dto.error.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.dto.error.NotAuthorizedUserException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ShoppingCartExceptionHandler {

    private final ShoppingCartErrorFactory errorFactory;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String reason = ex.getReason();

        if (status == HttpStatus.UNAUTHORIZED) {
            log.warn("{}: {}", status, reason);
            NotAuthorizedUserException body = errorFactory.notAuthorized(reason);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        if (status == HttpStatus.BAD_REQUEST) {
            log.warn("{}: {}", status, reason);
            NoProductsInShoppingCartException body = errorFactory.badRequest("Ошибка запроса", reason);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        throw ex;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<NoProductsInShoppingCartException> handleValidationExceptions(Exception ex) {
        log.warn("400 BAD_REQUEST: {}", ex.getMessage());
        NoProductsInShoppingCartException body = errorFactory.badRequest("Ошибка запроса", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
