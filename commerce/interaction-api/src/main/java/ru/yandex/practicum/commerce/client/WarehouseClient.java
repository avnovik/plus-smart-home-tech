package ru.yandex.practicum.commerce.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseApi {
}
