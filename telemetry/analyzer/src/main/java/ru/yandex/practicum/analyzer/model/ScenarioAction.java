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
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Связь сценария с датчиком и действием.
 */
@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@NoArgsConstructor
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @MapsId("scenarioId")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @MapsId("sensorId")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    @MapsId("actionId")
    private Action action;

    public ScenarioAction(Scenario scenario, Sensor sensor, Action action) {
        this.id = new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId());
        this.scenario = scenario;
        this.sensor = sensor;
        this.action = action;
    }

    /**
     * Composite key для {@link ScenarioAction}.
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScenarioActionId implements Serializable {

        @Column(name = "scenario_id")
        private Long scenarioId;

        @Column(name = "sensor_id")
        private String sensorId;

        @Column(name = "action_id")
        private Long actionId;

        public ScenarioActionId(Long scenarioId, String sensorId, Long actionId) {
            this.scenarioId = scenarioId;
            this.sensorId = sensorId;
            this.actionId = actionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ScenarioActionId that = (ScenarioActionId) o;
            return Objects.equals(scenarioId, that.scenarioId)
                    && Objects.equals(sensorId, that.sensorId)
                    && Objects.equals(actionId, that.actionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scenarioId, sensorId, actionId);
        }
    }
}
