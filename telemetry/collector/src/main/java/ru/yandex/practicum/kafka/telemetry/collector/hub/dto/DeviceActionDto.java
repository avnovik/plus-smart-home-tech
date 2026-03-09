package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

/**
 * DTO действия устройства в сценарии.
 */
public class DeviceActionDto {

    private String sensorId;

    private ActionTypeDto type;

    private Integer value;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public ActionTypeDto getType() {
        return type;
    }

    public void setType(ActionTypeDto type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
