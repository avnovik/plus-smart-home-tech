package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * DTO события климатического датчика.
 */
@Data
public class ClimateSensorEventDto extends SensorEventDto {

    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer humidity;

    @NotNull
    private Integer co2Level;
}
