package ru.yandex.practicum.commerce.dto.warehouse;

import java.util.UUID;

public record NewProductInWarehouseRequest(
        UUID productId,
        Boolean fragile,
        DimensionDto dimension,
        Double weight
) {
}
