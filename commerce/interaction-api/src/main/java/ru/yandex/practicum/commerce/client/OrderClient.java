package ru.yandex.practicum.commerce.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order")
public interface OrderClient extends OrderApi {
}
