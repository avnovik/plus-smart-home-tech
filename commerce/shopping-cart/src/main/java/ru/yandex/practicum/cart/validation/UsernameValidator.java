package ru.yandex.practicum.cart.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UsernameValidator {

    public void requireValid(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя пользователя не должно быть пустым");
        }
    }
}
