package ru.yandex.practicum.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.error.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.dto.error.NotAuthorizedUserException;

@Component
public class ShoppingCartErrorFactory {

    private static final String USERNAME_EMPTY_MESSAGE = "Имя пользователя не должно быть пустым";

    public NotAuthorizedUserException notAuthorized(String reason) {
        return new NotAuthorizedUserException(
                null,
                null,
                statusString(HttpStatus.UNAUTHORIZED),
                USERNAME_EMPTY_MESSAGE,
                reason,
                null,
                null
        );
    }

    public NoProductsInShoppingCartException badRequest(String userMessage, String reason) {
        return new NoProductsInShoppingCartException(
                null,
                null,
                statusString(HttpStatus.BAD_REQUEST),
                userMessage,
                reason,
                null,
                null
        );
    }

    private String statusString(HttpStatus status) {
        return status.value() + " " + status.name();
    }
}
