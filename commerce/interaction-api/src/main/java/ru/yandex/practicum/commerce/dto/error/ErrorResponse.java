package ru.yandex.practicum.commerce.dto.error;

public record ErrorResponse(
        String httpStatus,
        String userMessage,
        String message
) {
}
