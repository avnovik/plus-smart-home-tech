package ru.yandex.practicum.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.OrderApi;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.OrderEntity;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("getClientOrders: username={}", username);
        return orderService.getClientOrders(username)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("createNewOrder: cartId={}", request.shoppingCart().shoppingCartId());
        OrderEntity order = orderService.createNewOrder(request);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("productReturn: orderId={}", request.orderId());
        OrderEntity order = orderService.productReturn(request);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("payment: orderId={}", orderId);
        OrderEntity order = orderService.payment(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("paymentFailed: orderId={}", orderId);
        OrderEntity order = orderService.paymentFailed(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("delivery: orderId={}", orderId);
        OrderEntity order = orderService.delivery(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("deliveryFailed: orderId={}", orderId);
        OrderEntity order = orderService.deliveryFailed(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("complete: orderId={}", orderId);
        OrderEntity order = orderService.complete(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("calculateTotalCost: orderId={}", orderId);
        OrderEntity order = orderService.calculateTotalCost(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("calculateDeliveryCost: orderId={}", orderId);
        OrderEntity order = orderService.calculateDeliveryCost(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("assembly: orderId={}", orderId);
        OrderEntity order = orderService.assembly(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("assemblyFailed: orderId={}", orderId);
        OrderEntity order = orderService.assemblyFailed(orderId);
        return orderMapper.toDto(order);
    }
}
