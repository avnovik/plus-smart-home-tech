package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.client.OrderClient;
import ru.yandex.practicum.commerce.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.payment.model.PaymentEntity;
import ru.yandex.practicum.payment.model.PaymentStatus;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    private static final String NOT_ENOUGH_INFO_REASON = "Недостаточно информации в заказе для расчёта";
    private static final String ORDER_NOT_FOUND_REASON = "Заказ не найден";

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        if (order == null || order.products() == null || order.products().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_INFO_REASON);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : order.products().entrySet()) {
            UUID productId = entry.getKey();
            Long qty = entry.getValue();
            if (productId == null || qty == null) {
                continue;
            }

            ProductDto product = shoppingStoreClient.getProduct(productId);
            if (product == null || product.price() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_INFO_REASON);
            }

            total = total.add(product.price().multiply(BigDecimal.valueOf(qty)));
        }

        return total;
    }

    @Transactional(readOnly = true)
    public BigDecimal totalCost(OrderDto order) {
        BigDecimal productTotal = productCost(order);

        if (order.deliveryPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_INFO_REASON);
        }

        BigDecimal deliveryTotal = BigDecimal.valueOf(order.deliveryPrice());
        BigDecimal feeTotal = productTotal.multiply(VAT_RATE);

        return productTotal.add(feeTotal).add(deliveryTotal);
    }

    @Transactional
    public PaymentEntity payment(OrderDto order) {
        if (order == null || order.orderId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_INFO_REASON);
        }

        BigDecimal productTotal = productCost(order);

        if (order.deliveryPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_INFO_REASON);
        }

        BigDecimal deliveryTotal = BigDecimal.valueOf(order.deliveryPrice());
        BigDecimal feeTotal = productTotal.multiply(VAT_RATE);
        BigDecimal totalPayment = productTotal.add(feeTotal).add(deliveryTotal);

        PaymentEntity entity = new PaymentEntity();
        entity.setId(UUID.randomUUID());
        entity.setOrderId(order.orderId());
        entity.setProductTotal(scaleMoney(productTotal));
        entity.setDeliveryTotal(scaleMoney(deliveryTotal));
        entity.setFeeTotal(scaleMoney(feeTotal));
        entity.setTotalPayment(scaleMoney(totalPayment));
        entity.setStatus(PaymentStatus.PENDING);

        return paymentRepository.saveAndFlush(entity);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND_REASON));

        entity.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.saveAndFlush(entity);

        orderClient.payment(entity.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND_REASON));

        entity.setStatus(PaymentStatus.FAILED);
        paymentRepository.saveAndFlush(entity);

        orderClient.paymentFailed(entity.getOrderId());
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
