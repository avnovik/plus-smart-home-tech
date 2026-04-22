package ru.yandex.practicum.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.cart.model.CartState;
import ru.yandex.practicum.cart.model.ShoppingCartEntity;
import ru.yandex.practicum.cart.model.ShoppingCartItemEntity;
import ru.yandex.practicum.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Transactional
    public ShoppingCartEntity getOrCreateActiveCart(String username) {
        return shoppingCartRepository.findByUsernameAndState(username, CartState.ACTIVE)
                .orElseGet(() -> shoppingCartRepository.saveAndFlush(new ShoppingCartEntity(UUID.randomUUID(), username, CartState.ACTIVE)));
    }

    @Transactional
    public ShoppingCartEntity addProducts(String username, Map<UUID, Long> products) {
        ShoppingCartEntity cart = getOrCreateActiveCart(username);

        if (products == null || products.isEmpty()) {
            return cart;
        }

        warehouseClient.checkProductQuantityEnoughForShoppingCart(new ShoppingCartDto(cart.getId(), products));

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long addQty = entry.getValue();
            if (addQty == null) {
                continue;
            }
            int add = toPositiveInt(addQty);

            ShoppingCartItemEntity existing = findItem(cart, productId);
            if (existing == null) {
                cart.getItems().add(new ShoppingCartItemEntity(cart, productId, add));
            } else {
                existing.setQuantity(existing.getQuantity() + add);
            }
        }

        return shoppingCartRepository.saveAndFlush(cart);
    }

    @Transactional
    public ShoppingCartEntity removeProducts(String username, List<UUID> productIds) {
        ShoppingCartEntity cart = getOrCreateActiveCart(username);

        if (productIds == null || productIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет искомых товаров в корзине");
        }

        boolean removedAny = false;

        Set<UUID> ids = new HashSet<>(productIds);

        Iterator<ShoppingCartItemEntity> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            ShoppingCartItemEntity item = iterator.next();
            if (ids.contains(item.getId().getProductId())) {
                iterator.remove();
                removedAny = true;
            }
        }

        if (!removedAny) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет искомых товаров в корзине");
        }

        return shoppingCartRepository.saveAndFlush(cart);
    }

    @Transactional
    public ShoppingCartEntity changeQuantity(String username, UUID productId, Long newQuantity) {
        ShoppingCartEntity cart = getOrCreateActiveCart(username);
        ShoppingCartItemEntity item = findItem(cart, productId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет искомых товаров в корзине");
        }

        int qty = toPositiveInt(newQuantity);
        warehouseClient.checkProductQuantityEnoughForShoppingCart(new ShoppingCartDto(cart.getId(), Map.of(productId, (long) qty)));
        item.setQuantity(qty);
        return shoppingCartRepository.saveAndFlush(cart);
    }

    @Transactional
    public void deactivateCart(String username) {
        ShoppingCartEntity cart = getOrCreateActiveCart(username);
        cart.setState(CartState.DEACTIVATED);
        shoppingCartRepository.saveAndFlush(cart);
    }

    private ShoppingCartItemEntity findItem(ShoppingCartEntity cart, UUID productId) {
        for (ShoppingCartItemEntity item : cart.getItems()) {
            if (item.getId().getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private int toPositiveInt(Long value) {
        if (value == null || value < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное количество товара");
        }
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное количество товара");
        }
    }
}
