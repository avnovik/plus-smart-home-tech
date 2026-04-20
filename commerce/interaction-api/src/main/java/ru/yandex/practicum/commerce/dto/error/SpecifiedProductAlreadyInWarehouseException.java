package ru.yandex.practicum.commerce.dto.error;

import java.util.List;

public record SpecifiedProductAlreadyInWarehouseException(
        Object cause,
        List<Object> stackTrace,
        String httpStatus,
        String userMessage,
        String message,
        List<Object> suppressed,
        String localizedMessage
) {
}
