package ru.yandex.practicum.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.error.ErrorResponse;

@Component
public class DeliveryErrorFactory {

    public ErrorResponse notFound(String userMessage, String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.NOT_FOUND),
                userMessage,
                reason
        );
    }

    public ErrorResponse badRequest(String userMessage, String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.BAD_REQUEST),
                userMessage,
                reason
        );
    }

    private String statusString(HttpStatus status) {
        return status.value() + " " + status.name();
    }
}
