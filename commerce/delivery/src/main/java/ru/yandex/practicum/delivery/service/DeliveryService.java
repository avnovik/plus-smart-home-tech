package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.client.OrderClient;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.common.AddressDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.delivery.model.DeliveryEntity;
import ru.yandex.practicum.delivery.model.DeliveryState;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final double BASE_COST = 5.0;

    private static final String WAREHOUSE_ADDRESS_1_MARKER = "ADDRESS_1";
    private static final String WAREHOUSE_ADDRESS_2_MARKER = "ADDRESS_2";

    private static final double WAREHOUSE_ADDRESS_1_MULTIPLIER = 1.0;
    private static final double WAREHOUSE_ADDRESS_2_MULTIPLIER = 2.0;

    private static final double FRAGILE_COEFFICIENT = 0.2;
    private static final double WEIGHT_COEFFICIENT = 0.3;
    private static final double VOLUME_COEFFICIENT = 0.2;
    private static final double STREET_MISMATCH_COEFFICIENT = 0.2;

    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Transactional
    public DeliveryEntity planDelivery(DeliveryEntity delivery) {
        if (delivery.getId() == null) {
            delivery.setId(UUID.randomUUID());
        }
        if (delivery.getState() == null) {
            delivery.setState(DeliveryState.CREATED);
        }
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        DeliveryEntity delivery = getByOrderId(orderId);
        delivery.setState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        orderClient.assembly(orderId);

        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getId()));
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        DeliveryEntity delivery = getByOrderId(orderId);
        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(orderId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        DeliveryEntity delivery = getByOrderId(orderId);
        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(orderId);
    }

    @Transactional(readOnly = true)
    public double deliveryCost(OrderDto order) {
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Заказ не задан");
        }

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        DeliveryEntity delivery = deliveryRepository.findByOrderId(order.orderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Доставка не найдена"));

        String warehouseStreet = warehouseAddress == null ? null : warehouseAddress.street();
        String deliveryStreet = null;

        if (delivery != null) {
            deliveryStreet = delivery.getToStreet();
        }

        double cost = BASE_COST;

        String warehouseStreetMarker = warehouseAddress == null || warehouseAddress.street() == null
                ? ""
                : warehouseAddress.street();

        if (warehouseStreetMarker.contains(WAREHOUSE_ADDRESS_2_MARKER)) {
            cost = cost * WAREHOUSE_ADDRESS_2_MULTIPLIER;
        } else if (warehouseStreetMarker.contains(WAREHOUSE_ADDRESS_1_MARKER)) {
            cost = cost * WAREHOUSE_ADDRESS_1_MULTIPLIER;
        }
        cost = cost + BASE_COST;

        if (Boolean.TRUE.equals(order.fragile())) {
            cost = cost + (cost * FRAGILE_COEFFICIENT);
        }

        if (order.deliveryWeight() != null) {
            cost = cost + (order.deliveryWeight() * WEIGHT_COEFFICIENT);
        }

        if (order.deliveryVolume() != null) {
            cost = cost + (order.deliveryVolume() * VOLUME_COEFFICIENT);
        }

        if (warehouseStreet != null && deliveryStreet != null && !warehouseStreet.equalsIgnoreCase(deliveryStreet)) {
            cost = cost + (cost * STREET_MISMATCH_COEFFICIENT);
        }

        return cost;
    }

    private DeliveryEntity getByOrderId(UUID orderId) {
        if (orderId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Идентификатор заказа не задан");
        }

        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Доставка не найдена"));
    }
}
