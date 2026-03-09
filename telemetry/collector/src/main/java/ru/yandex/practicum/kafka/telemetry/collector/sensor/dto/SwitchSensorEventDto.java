package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO события умного переключателя.
 */
public class SwitchSensorEventDto extends SensorEventDto {

    @NotNull
    private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
