package ru.yandex.practicum.order.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.order.model.OrderEntity;
import ru.yandex.practicum.order.model.OrderItemEntity;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDto toDto(OrderEntity order) {
        Map<UUID, Long> products = order.getItems()
                .stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        OrderItemEntity::getQuantity
                ));

        return new OrderDto(
                order.getId(),
                order.getShoppingCartId(),
                products,
                order.getPaymentId(),
                order.getDeliveryId(),
                ru.yandex.practicum.commerce.dto.order.OrderState.valueOf(order.getState().name()),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice() == null ? null : order.getTotalPrice().doubleValue(),
                order.getDeliveryPrice() == null ? null : order.getDeliveryPrice().doubleValue(),
                order.getProductPrice() == null ? null : order.getProductPrice().doubleValue()
        );
    }
}
