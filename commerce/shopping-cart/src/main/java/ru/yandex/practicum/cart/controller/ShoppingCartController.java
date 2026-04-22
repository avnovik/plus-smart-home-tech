package ru.yandex.practicum.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.cart.model.ShoppingCartEntity;
import ru.yandex.practicum.cart.service.ShoppingCartService;
import ru.yandex.practicum.cart.validation.UsernameValidator;
import ru.yandex.practicum.commerce.client.ShoppingCartApi;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UsernameValidator usernameValidator;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        usernameValidator.requireValid(username);
        log.info("getShoppingCart: username={}", username);
        ShoppingCartEntity cart = shoppingCartService.getOrCreateActiveCart(username);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        usernameValidator.requireValid(username);
        log.info("addProductToShoppingCart: username={}, productsCount={}", username, products == null ? 0 : products.size());
        ShoppingCartEntity cart = shoppingCartService.addProducts(username, products);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        usernameValidator.requireValid(username);
        log.info("deactivateCurrentShoppingCart: username={}", username);
        shoppingCartService.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        usernameValidator.requireValid(username);
        log.info("removeFromShoppingCart: username={}, productIdsCount={}", username, productIds == null ? 0 : productIds.size());
        ShoppingCartEntity cart = shoppingCartService.removeProducts(username, productIds);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        usernameValidator.requireValid(username);
        log.info("changeProductQuantity: username={}, productId={}, newQuantity={}", username, request.productId(), request.newQuantity());
        ShoppingCartEntity cart = shoppingCartService.changeQuantity(username, request.productId(), request.newQuantity());
        return shoppingCartMapper.toDto(cart);
    }
}
