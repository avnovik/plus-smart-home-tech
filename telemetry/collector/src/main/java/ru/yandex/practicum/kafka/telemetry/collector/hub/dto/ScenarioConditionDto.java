package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

/**
 * DTO условия активации сценария.
 */
public class ScenarioConditionDto {

    private String sensorId;

    private ConditionTypeDto type;

    private ConditionOperationDto operation;

    private Integer value;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public ConditionTypeDto getType() {
        return type;
    }

    public void setType(ConditionTypeDto type) {
        this.type = type;
    }

    public ConditionOperationDto getOperation() {
        return operation;
    }

    public void setOperation(ConditionOperationDto operation) {
        this.operation = operation;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
