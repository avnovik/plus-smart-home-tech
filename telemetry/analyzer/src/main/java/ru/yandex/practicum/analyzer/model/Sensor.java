package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность датчика, привязанного к хабу.
 */
@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
public class Sensor {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "hub_id")
    private String hubId;

    public Sensor(String id, String hubId) {
        this.id = id;
        this.hubId = hubId;
    }
}
