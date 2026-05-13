package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "order_booking_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderBookingItemEntity {

    @EmbeddedId
    private OrderBookingItemId id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderBookingEntity booking;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    public OrderBookingItemEntity(OrderBookingEntity booking, UUID productId, Long quantity) {
        this.booking = booking;
        this.id = new OrderBookingItemId(booking.getOrderId(), productId);
        this.quantity = quantity;
    }
}
