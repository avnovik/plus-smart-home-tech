package ru.yandex.practicum.commerce.dto.store;

import java.util.List;

public record PageProductDto(
        Long totalElements,
        Integer totalPages,
        Boolean first,
        Boolean last,
        Integer size,
        List<ProductDto> content,
        Integer number,
        List<SortObject> sort,
        Integer numberOfElements,
        PageableObject pageable,
        Boolean empty
) {
}
