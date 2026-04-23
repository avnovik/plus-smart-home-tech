package ru.yandex.practicum.warehouse.exception;

public enum WarehouseErrorReasons {

    PRODUCT_ALREADY_REGISTERED("Товар уже зарегистрирован на складе"),
    NO_PRODUCT_INFO("Нет информации о товаре на складе"),
    PRODUCT_NOT_IN_WAREHOUSE("Товара нет на складе"),
    LOW_QUANTITY("Недостаточно товара на складе"),
    INVALID_PRODUCT_PARAMS("Некорректные параметры товара");

    private final String message;

    WarehouseErrorReasons(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
