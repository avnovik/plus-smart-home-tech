package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryApi {

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto request);

    @PostMapping("/api/v1/delivery/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/cost")
    Double deliveryCost(@Valid @RequestBody OrderDto order);
}
