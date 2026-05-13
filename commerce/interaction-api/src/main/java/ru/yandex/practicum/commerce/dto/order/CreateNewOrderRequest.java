package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.commerce.dto.common.AddressDto;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;

public record CreateNewOrderRequest(
        @NotNull @Valid ShoppingCartDto shoppingCart,
        @NotNull @Valid AddressDto deliveryAddress
) {
}
