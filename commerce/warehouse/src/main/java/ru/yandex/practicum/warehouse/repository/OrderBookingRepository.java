package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.OrderBookingEntity;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBookingEntity, UUID> {
}
