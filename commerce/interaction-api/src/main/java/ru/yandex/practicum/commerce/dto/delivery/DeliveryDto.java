package ru.yandex.practicum.commerce.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.commerce.dto.common.AddressDto;

import java.util.UUID;

public record DeliveryDto(
        UUID deliveryId,
        @NotNull @Valid AddressDto fromAddress,
        @NotNull @Valid AddressDto toAddress,
        @NotNull UUID orderId,
        @NotNull DeliveryState deliveryState
) {
}
