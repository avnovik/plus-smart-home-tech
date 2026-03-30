package ru.yandex.practicum.kafka.telemetry.collector.exceptions;

/**
 * Минимальный DTO для ответа об ошибке.
 */
public class ErrorResponse {

    private final String error;
    public ErrorResponse(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }
}
