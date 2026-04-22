package ru.yandex.practicum.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.cart.model.CartState;
import ru.yandex.practicum.cart.model.ShoppingCartEntity;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {

    Optional<ShoppingCartEntity> findByUsernameAndState(String username, CartState state);
}
