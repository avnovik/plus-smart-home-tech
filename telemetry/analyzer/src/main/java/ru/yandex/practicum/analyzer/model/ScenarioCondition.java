package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Связь сценария с датчиком и условием.
 */
@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @MapsId("scenarioId")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @MapsId("sensorId")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", nullable = false)
    @MapsId("conditionId")
    private Condition condition;

    public ScenarioCondition() {
    }

    public ScenarioCondition(Scenario scenario, Sensor sensor, Condition condition) {
        this.id = new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId());
        this.scenario = scenario;
        this.sensor = sensor;
        this.condition = condition;
    }

    /**
     * Composite key для {@link ScenarioCondition}.
     */
    @Embeddable
    @Getter
    @Setter
    public static class ScenarioConditionId implements Serializable {

        @Column(name = "scenario_id")
        private Long scenarioId;

        @Column(name = "sensor_id")
        private String sensorId;

        @Column(name = "condition_id")
        private Long conditionId;

        public ScenarioConditionId() {
        }

        public ScenarioConditionId(Long scenarioId, String sensorId, Long conditionId) {
            this.scenarioId = scenarioId;
            this.sensorId = sensorId;
            this.conditionId = conditionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ScenarioConditionId that = (ScenarioConditionId) o;
            return Objects.equals(scenarioId, that.scenarioId)
                    && Objects.equals(sensorId, that.sensorId)
                    && Objects.equals(conditionId, that.conditionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scenarioId, sensorId, conditionId);
        }
    }
}
