package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import lombok.Data;

/**
 * DTO условия активации сценария.
 */
@Data
public class ScenarioConditionDto {

    private String sensorId;

    private ConditionTypeDto type;

    private ConditionOperationDto operation;

    private Integer value;
}
