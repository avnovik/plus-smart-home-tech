package ru.yandex.practicum.kafka.telemetry.collector.exceptions;

import lombok.Getter;

/**
 * Минимальный DTO для ответа об ошибке.
 */
@Getter
public class ErrorResponse {

    private final String error;
    public ErrorResponse(String error) {
        this.error = error;
    }
}
