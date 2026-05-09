package ru.yandex.practicum.commerce.dto.delivery;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
