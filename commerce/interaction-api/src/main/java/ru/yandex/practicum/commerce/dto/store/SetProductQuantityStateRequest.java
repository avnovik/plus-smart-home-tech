package ru.yandex.practicum.commerce.dto.store;

import java.util.UUID;

public record SetProductQuantityStateRequest(
        UUID productId,
        QuantityState quantityState
) {
}
