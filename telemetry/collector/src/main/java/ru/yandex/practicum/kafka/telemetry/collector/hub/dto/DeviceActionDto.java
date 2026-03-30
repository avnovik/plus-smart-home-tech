package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import lombok.Data;

/**
 * DTO действия устройства в сценарии.
 */
@Data
public class DeviceActionDto {

    private String sensorId;

    private ActionTypeDto type;

    private Integer value;
}
