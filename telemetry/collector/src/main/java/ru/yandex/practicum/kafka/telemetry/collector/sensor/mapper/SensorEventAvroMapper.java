package ru.yandex.practicum.kafka.telemetry.collector.sensor.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.ClimateSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.LightSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.MotionSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SwitchSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.TemperatureSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

/**
 * Маппер DTO события датчика в Avro.
 */
@Component
public class SensorEventAvroMapper {

    public SensorEventAvro toAvro(SensorEventDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Sensor event is null");
        }

        Instant timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();

        return toAvro(dto, timestamp);
    }

    public SensorEventAvro toAvro(SensorEventDto dto, Instant timestamp) {
        if (dto == null) {
            throw new IllegalArgumentException("Sensor event is null");
        }

        if (timestamp == null) {
            timestamp = Instant.now();
        }

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(timestamp)
                .setPayload(toPayload(dto, timestamp))
                .build();
    }

    private Object toPayload(SensorEventDto dto, Instant timestamp) {
        if (dto instanceof ClimateSensorEventDto climate) {
            return new ClimateSensorAvro(
                    climate.getTemperatureC(),
                    climate.getHumidity(),
                    climate.getCo2Level()
            );
        }

        if (dto instanceof LightSensorEventDto light) {
            int linkQuality = light.getLinkQuality() != null ? light.getLinkQuality() : 0;
            int luminosity = light.getLuminosity() != null ? light.getLuminosity() : 0;
            return new LightSensorAvro(
                    linkQuality,
                    luminosity
            );
        }

        if (dto instanceof MotionSensorEventDto motion) {
            return new MotionSensorAvro(
                    motion.getLinkQuality(),
                    motion.getMotion(),
                    motion.getVoltage()
            );
        }

        if (dto instanceof SwitchSensorEventDto sw) {
            return new SwitchSensorAvro(sw.getState());
        }

        if (dto instanceof TemperatureSensorEventDto temperature) {
            return new TemperatureSensorAvro(
                    temperature.getId(),
                    temperature.getHubId(),
                    timestamp,
                    temperature.getTemperatureC(),
                    temperature.getTemperatureF()
            );
        }

        throw new IllegalArgumentException("Unknown sensor event dto: " + dto.getClass().getName());
    }
}
