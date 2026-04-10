package ru.yandex.practicum.aggregator.snapshot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensorsSnapshotAggregatorTest {

    @Test
    @DisplayName("Первое событие по хабу создаёт снапшот")
    void createsSnapshotOnFirstEvent() {
        SensorsSnapshotAggregator aggregator = new SensorsSnapshotAggregator();

        SensorEventAvro event = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(1000), new MotionSensorAvro(10, true, 220));
        assertTrue(aggregator.updateState(event).isPresent());
    }

    @Test
    @DisplayName("Дубликат события (тот же payload) не обновляет снапшот")
    void ignoresSamePayload() {
        SensorsSnapshotAggregator aggregator = new SensorsSnapshotAggregator();

        MotionSensorAvro payload = new MotionSensorAvro(10, true, 220);
        SensorEventAvro event1 = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(1000), payload);
        SensorEventAvro event2 = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(2000), payload);

        assertTrue(aggregator.updateState(event1).isPresent());
        assertFalse(aggregator.updateState(event2).isPresent());
    }

    @Test
    @DisplayName("Старое событие не обновляет состояние датчика")
    void ignoresOlderEvent() {
        SensorsSnapshotAggregator aggregator = new SensorsSnapshotAggregator();

        SensorEventAvro newer = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(2000), new MotionSensorAvro(10, true, 220));
        SensorEventAvro older = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(1000), new MotionSensorAvro(11, false, 230));

        assertTrue(aggregator.updateState(newer).isPresent());
        assertFalse(aggregator.updateState(older).isPresent());
    }

    @Test
    @DisplayName("Новое событие с изменившимся payload обновляет снапшот")
    void updatesOnNewPayload() {
        SensorsSnapshotAggregator aggregator = new SensorsSnapshotAggregator();

        SensorEventAvro event1 = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(1000), new MotionSensorAvro(10, true, 220));
        SensorEventAvro event2 = sensorEvent("hub-1", "sensor-1", Instant.ofEpochMilli(2000), new MotionSensorAvro(11, true, 220));

        assertTrue(aggregator.updateState(event1).isPresent());
        assertTrue(aggregator.updateState(event2).isPresent());
    }

    private static SensorEventAvro sensorEvent(String hubId, String sensorId, Instant timestamp, Object payload) {
        return SensorEventAvro.newBuilder()
                .setId(sensorId)
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }
}
