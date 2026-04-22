package ru.yandex.practicum.warehouse.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.dto.error.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.dto.error.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.dto.error.SpecifiedProductAlreadyInWarehouseException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class WarehouseExceptionHandler {

    private final WarehouseErrorFactory errorFactory;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String reason = ex.getReason();

        if (status != HttpStatus.BAD_REQUEST) {
            throw ex;
        }

        log.warn("{}: {}", status, reason);

        if (WarehouseErrorReasons.PRODUCT_ALREADY_REGISTERED.message().equals(reason)) {
            SpecifiedProductAlreadyInWarehouseException body = errorFactory.productAlreadyRegistered(reason);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        if (WarehouseErrorReasons.NO_PRODUCT_INFO.message().equals(reason)) {
            NoSpecifiedProductInWarehouseException body = errorFactory.noProductInfo(reason);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        ProductInShoppingCartLowQuantityInWarehouse body = errorFactory.lowQuantity(reason);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ProductInShoppingCartLowQuantityInWarehouse> handleValidationExceptions(Exception ex) {
        log.warn("400 BAD_REQUEST: {}", ex.getMessage());
        ProductInShoppingCartLowQuantityInWarehouse body = errorFactory.lowQuantity(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
