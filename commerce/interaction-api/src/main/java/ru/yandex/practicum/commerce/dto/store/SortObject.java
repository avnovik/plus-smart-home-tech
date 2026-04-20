package ru.yandex.practicum.commerce.dto.store;

public record SortObject(
        String direction,
        String nullHandling,
        Boolean ascending,
        String property,
        Boolean ignoreCase
) {
}
