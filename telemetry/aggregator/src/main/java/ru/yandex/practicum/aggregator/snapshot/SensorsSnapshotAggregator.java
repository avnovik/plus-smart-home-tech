package ru.yandex.practicum.aggregator.snapshot;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Агрегирует входящие события датчиков ({@link SensorEventAvro}) в снапшоты состояния хаба ({@link SensorsSnapshotAvro}).
 * <p>
 * Снапшот обновляется только если:
 * 1) для датчика ещё нет состояния в снапшоте, или</li>
 * 2) событие новее текущего состояния датчика, и payload отличается.</li>
 */
@Component
public class SensorsSnapshotAggregator {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    /**
     * Обновляет состояние снапшота для хаба из события датчика.
     */
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (event == null) {
            throw new IllegalArgumentException("Sensor event is null");
        }

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), hubId -> newSnapshot(hubId));

        Map<String, SensorStateAvro> stateBySensorId = snapshot.getSensorsState();
        if (stateBySensorId == null) {
            stateBySensorId = new HashMap<>();
            snapshot.setSensorsState(stateBySensorId);
        }

        String sensorId = event.getId();
        SensorStateAvro oldState = stateBySensorId.get(sensorId);
        if (oldState != null) {
            Instant oldTimestamp = oldState.getTimestamp();
            if (oldTimestamp != null && oldTimestamp.isAfter(event.getTimestamp())) {
                return Optional.empty();
            }

            if (Objects.equals(oldState.getData(), event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro(event.getTimestamp(), event.getPayload());
        stateBySensorId.put(sensorId, newState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro newSnapshot(String hubId) {
        return new SensorsSnapshotAvro(hubId, Instant.EPOCH, new HashMap<>());
    }
}
