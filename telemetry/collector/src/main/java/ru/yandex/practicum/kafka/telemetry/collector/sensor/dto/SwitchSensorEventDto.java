package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO события умного переключателя.
 */
@Data
public class SwitchSensorEventDto extends SensorEventDto {

    @NotNull
    private Boolean state;
}
