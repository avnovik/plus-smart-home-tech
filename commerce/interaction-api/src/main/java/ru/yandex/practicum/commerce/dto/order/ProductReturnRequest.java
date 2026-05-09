package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;
import java.util.UUID;

public record ProductReturnRequest(
        UUID orderId,
        @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> products
) {
}
