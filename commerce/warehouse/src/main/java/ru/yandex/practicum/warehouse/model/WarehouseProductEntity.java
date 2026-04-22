package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseProductEntity {

    @Id
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "fragile", nullable = false)
    private Boolean fragile;

    @Column(name = "weight", nullable = false)
    private Long weight;

    @Column(name = "width", nullable = false)
    private Long width;

    @Column(name = "height", nullable = false)
    private Long height;

    @Column(name = "depth", nullable = false)
    private Long depth;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
