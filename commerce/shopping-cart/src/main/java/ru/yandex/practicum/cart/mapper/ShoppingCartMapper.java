package ru.yandex.practicum.cart.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.model.ShoppingCartEntity;
import ru.yandex.practicum.cart.model.ShoppingCartItemEntity;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCartEntity entity) {
        Map<UUID, Long> products = new HashMap<>();
        for (ShoppingCartItemEntity item : entity.getItems()) {
            products.put(item.getId().getProductId(), item.getQuantity().longValue());
        }
        return new ShoppingCartDto(entity.getId(), products);
    }
}
