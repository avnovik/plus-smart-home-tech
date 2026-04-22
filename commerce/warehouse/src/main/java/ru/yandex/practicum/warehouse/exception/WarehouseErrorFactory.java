package ru.yandex.practicum.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.error.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.dto.error.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.dto.error.SpecifiedProductAlreadyInWarehouseException;

@Component
public class WarehouseErrorFactory {

    public SpecifiedProductAlreadyInWarehouseException productAlreadyRegistered(String reason) {
        return new SpecifiedProductAlreadyInWarehouseException(
                null,
                null,
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.PRODUCT_ALREADY_REGISTERED.message(),
                reason,
                null,
                null
        );
    }

    public NoSpecifiedProductInWarehouseException noProductInfo(String reason) {
        return new NoSpecifiedProductInWarehouseException(
                null,
                null,
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.NO_PRODUCT_INFO.message(),
                reason,
                null,
                null
        );
    }

    public ProductInShoppingCartLowQuantityInWarehouse lowQuantity(String reason) {
        return new ProductInShoppingCartLowQuantityInWarehouse(
                null,
                null,
                statusString(HttpStatus.BAD_REQUEST),
                WarehouseErrorReasons.LOW_QUANTITY.message(),
                reason,
                null,
                null
        );
    }

    private String statusString(HttpStatus status) {
        return status.value() + " " + status.name();
    }
}
