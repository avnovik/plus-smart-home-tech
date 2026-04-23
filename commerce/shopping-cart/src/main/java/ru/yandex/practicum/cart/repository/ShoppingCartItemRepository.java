package ru.yandex.practicum.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.cart.model.ShoppingCartItemEntity;
import ru.yandex.practicum.cart.model.ShoppingCartItemId;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItemEntity, ShoppingCartItemId> {
}
