package ru.yandex.practicum.commerce.dto.warehouse;

import java.util.UUID;

public record AddProductToWarehouseRequest(
        UUID productId,
        Long quantity
) {
}
