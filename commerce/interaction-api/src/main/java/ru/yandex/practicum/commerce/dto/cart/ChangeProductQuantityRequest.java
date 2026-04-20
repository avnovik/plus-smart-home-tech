package ru.yandex.practicum.commerce.dto.cart;

import java.util.UUID;

public record ChangeProductQuantityRequest(
        UUID productId,
        Long newQuantity
) {
}
