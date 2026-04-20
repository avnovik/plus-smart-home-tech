package ru.yandex.practicum.commerce.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreApi {
}
