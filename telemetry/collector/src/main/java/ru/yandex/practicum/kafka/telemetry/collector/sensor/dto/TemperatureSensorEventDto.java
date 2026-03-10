package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO события датчика температуры.
 */
@Data
public class TemperatureSensorEventDto extends SensorEventDto {

    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer temperatureF;
}
