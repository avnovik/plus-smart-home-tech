package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.DeliveryApi;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.DeliveryEntity;
import ru.yandex.practicum.delivery.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryDto planDelivery(@NotNull DeliveryDto request) {
        log.info("planDelivery: orderId={}", request.orderId());
        DeliveryEntity entity = deliveryMapper.toEntity(request);
        DeliveryEntity saved = deliveryService.planDelivery(entity);
        return deliveryMapper.toDto(saved);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.info("deliverySuccessful: orderId={}", orderId);
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.info("deliveryPicked: orderId={}", orderId);
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.info("deliveryFailed: orderId={}", orderId);
        deliveryService.deliveryFailed(orderId);
    }

    @Override
    public Double deliveryCost(OrderDto order) {
        log.info("deliveryCost: orderId={}", order == null ? null : order.orderId());
        return deliveryService.deliveryCost(order);
    }
}
