package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.WarehouseProductEntity;

import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProductEntity, UUID> {
}
