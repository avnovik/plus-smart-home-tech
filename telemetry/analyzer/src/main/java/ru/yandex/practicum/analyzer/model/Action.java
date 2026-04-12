package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Действие, которое нужно выполнить при активации сценария.
 */
@Entity
@Table(name = "actions")
@Getter
@Setter
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ActionType type;

    @Column(name = "value")
    private Integer value;

    public Action() {
    }

    public Action(ActionType type, Integer value) {
        this.type = type;
        this.value = value;
    }
}
