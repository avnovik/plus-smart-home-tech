package ru.yandex.practicum.commerce.dto.common;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
