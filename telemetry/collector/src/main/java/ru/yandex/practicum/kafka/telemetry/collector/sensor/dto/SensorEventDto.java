package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * Базовый DTO события датчика.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEventDto.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEventDto.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEventDto.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEventDto.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEventDto.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@Data
public abstract class SensorEventDto {

    @NotBlank
    private String id;

    @NotBlank
    private String hubId;

    private Instant timestamp;

    @NotNull
    private SensorEventType type;
}
