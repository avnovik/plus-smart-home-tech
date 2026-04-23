package ru.yandex.practicum.commerce.dto.store;

import java.util.List;

public record PageableObject(
        Long offset,
        List<SortObject> sort,
        Boolean unpaged,
        Boolean paged,
        Integer pageNumber,
        Integer pageSize
) {
}
