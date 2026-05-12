package ru.yandex.practicum.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.error.ErrorResponse;

@Component
public class OrderErrorFactory {

    private static final String USERNAME_EMPTY_MESSAGE = "Имя пользователя не должно быть пустым";

    public ErrorResponse notAuthorized(String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.UNAUTHORIZED),
                USERNAME_EMPTY_MESSAGE,
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

    public ErrorResponse notFound(String userMessage, String reason) {
        return new ErrorResponse(
                statusString(HttpStatus.NOT_FOUND),
                userMessage,
                reason
        );
    }

    private String statusString(HttpStatus status) {
        return status.value() + " " + status.name();
    }
}
