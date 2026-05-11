package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter
@Setter
@NoArgsConstructor
public class OrderBookingEntity {

    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

     @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
     private Set<OrderBookingItemEntity> items = new HashSet<>();
}
