package ru.yandex.practicum.commerce.client;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-cart")
public interface ShoppingCartApi {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Long> products
    );

    @DeleteMapping
    void deactivateCurrentShoppingCart(@RequestParam("username") String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIds
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam("username") String username,
            @RequestBody ChangeProductQuantityRequest request
    );
}
