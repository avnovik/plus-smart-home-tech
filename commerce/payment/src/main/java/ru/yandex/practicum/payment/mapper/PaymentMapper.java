package ru.yandex.practicum.payment.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.model.PaymentEntity;

@Component
public class PaymentMapper {

    public PaymentDto toDto(PaymentEntity entity) {
        return new PaymentDto(
                entity.getId(),
                entity.getTotalPayment() == null ? null : entity.getTotalPayment().doubleValue(),
                entity.getDeliveryTotal() == null ? null : entity.getDeliveryTotal().doubleValue(),
                entity.getFeeTotal() == null ? null : entity.getFeeTotal().doubleValue()
        );
    }
}
