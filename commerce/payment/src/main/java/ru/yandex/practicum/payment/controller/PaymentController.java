package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.PaymentApi;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.PaymentEntity;
import ru.yandex.practicum.payment.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto payment(OrderDto order) {
        log.info("payment: orderId={}", order == null ? null : order.orderId());
        PaymentEntity entity = paymentService.payment(order);
        return paymentMapper.toDto(entity);
    }

    @Override
    public Double getTotalCost(OrderDto order) {
        log.info("getTotalCost: orderId={}", order == null ? null : order.orderId());
        return paymentService.totalCost(order).doubleValue();
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("paymentSuccess: paymentId={}", paymentId);
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public Double productCost(OrderDto order) {
        log.info("productCost: orderId={}", order == null ? null : order.orderId());
        return paymentService.productCost(order).doubleValue();
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("paymentFailed: paymentId={}", paymentId);
        paymentService.paymentFailed(paymentId);
    }
}
