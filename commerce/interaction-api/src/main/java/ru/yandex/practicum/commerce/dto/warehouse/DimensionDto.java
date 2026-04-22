package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DimensionDto(
        @NotNull @DecimalMin("1") Double width,
        @NotNull @DecimalMin("1") Double height,
        @NotNull @DecimalMin("1") Double depth
) {
}
