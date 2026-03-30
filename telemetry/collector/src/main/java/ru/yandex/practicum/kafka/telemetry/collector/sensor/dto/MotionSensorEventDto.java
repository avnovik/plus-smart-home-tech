package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO события датчика движения.
 */
@Data
public class MotionSensorEventDto extends SensorEventDto {

    @NotNull
    private Integer linkQuality;

    @NotNull
    private Boolean motion;

    @NotNull
    private Integer voltage;
}
