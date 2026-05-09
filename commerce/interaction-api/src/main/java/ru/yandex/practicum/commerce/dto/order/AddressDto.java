package ru.yandex.practicum.commerce.dto.order;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
