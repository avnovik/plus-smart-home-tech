package ru.yandex.practicum.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.error.ErrorResponse;

@Component
public class WarehouseErrorFactory {

    public ErrorResponse productAlreadyRegistered(String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.PRODUCT_ALREADY_REGISTERED.message(),
                reason
        );
    }

    public ErrorResponse noProductInfo(String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.NO_PRODUCT_INFO.message(),
                reason
        );
    }

    public ErrorResponse lowQuantity(String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.LOW_QUANTITY.message(),
                reason
        );
    }

    private String statusString(HttpStatus status) {
        return status.value() + " " + status.name();
    }
}
