package ru.yandex.practicum.commerce.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "delivery")
public interface DeliveryClient extends DeliveryApi {
}
