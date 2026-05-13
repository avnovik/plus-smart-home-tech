package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;
import java.util.UUID;

public record OrderDto(
        @NotNull UUID orderId,
        UUID shoppingCartId,
        @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> products,
        UUID paymentId,
        UUID deliveryId,
        OrderState state,
        Double deliveryWeight,
        Double deliveryVolume,
        Boolean fragile,
        Double totalPrice,
        Double deliveryPrice,
        Double productPrice
) {
}
