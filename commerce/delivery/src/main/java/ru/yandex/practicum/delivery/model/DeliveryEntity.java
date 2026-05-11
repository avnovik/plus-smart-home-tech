package ru.yandex.practicum.delivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private DeliveryState state;

    @Column(name = "from_country", nullable = false)
    private String fromCountry;

    @Column(name = "from_city", nullable = false)
    private String fromCity;

    @Column(name = "from_street", nullable = false)
    private String fromStreet;

    @Column(name = "from_house", nullable = false)
    private String fromHouse;

    @Column(name = "from_flat", nullable = false)
    private String fromFlat;

    @Column(name = "to_country", nullable = false)
    private String toCountry;

    @Column(name = "to_city", nullable = false)
    private String toCity;

    @Column(name = "to_street", nullable = false)
    private String toStreet;

    @Column(name = "to_house", nullable = false)
    private String toHouse;

    @Column(name = "to_flat", nullable = false)
    private String toFlat;
}
