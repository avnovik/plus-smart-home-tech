package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

import lombok.Data;

/**
 * DTO события датчика освещённости.
 */
@Data
public class LightSensorEventDto extends SensorEventDto {

    private Integer linkQuality;

    private Integer luminosity;
}
