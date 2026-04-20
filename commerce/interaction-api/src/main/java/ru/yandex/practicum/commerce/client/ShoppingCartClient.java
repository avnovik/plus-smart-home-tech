package ru.yandex.practicum.commerce.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient extends ShoppingCartApi {
}
