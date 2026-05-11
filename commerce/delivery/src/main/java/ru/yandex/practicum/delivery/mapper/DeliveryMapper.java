package ru.yandex.practicum.delivery.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.common.AddressDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.delivery.model.DeliveryEntity;
import ru.yandex.practicum.delivery.model.DeliveryState;

@Component
public class DeliveryMapper {

    public DeliveryDto toDto(DeliveryEntity entity) {
        return new DeliveryDto(
                entity.getId(),
                new AddressDto(entity.getFromCountry(), entity.getFromCity(), entity.getFromStreet(), entity.getFromHouse(), entity.getFromFlat()),
                new AddressDto(entity.getToCountry(), entity.getToCity(), entity.getToStreet(), entity.getToHouse(), entity.getToFlat()),
                entity.getOrderId(),
                ru.yandex.practicum.commerce.dto.delivery.DeliveryState.valueOf(entity.getState().name())
        );
    }

    public DeliveryEntity toEntity(DeliveryDto dto) {
        DeliveryEntity entity = new DeliveryEntity();
        entity.setId(dto.deliveryId());
        entity.setOrderId(dto.orderId());
        entity.setState(DeliveryState.valueOf(dto.deliveryState().name()));

        entity.setFromCountry(dto.fromAddress().country());
        entity.setFromCity(dto.fromAddress().city());
        entity.setFromStreet(dto.fromAddress().street());
        entity.setFromHouse(dto.fromAddress().house());
        entity.setFromFlat(dto.fromAddress().flat());

        entity.setToCountry(dto.toAddress().country());
        entity.setToCity(dto.toAddress().city());
        entity.setToStreet(dto.toAddress().street());
        entity.setToHouse(dto.toAddress().house());
        entity.setToFlat(dto.toAddress().flat());

        return entity;
    }
}
