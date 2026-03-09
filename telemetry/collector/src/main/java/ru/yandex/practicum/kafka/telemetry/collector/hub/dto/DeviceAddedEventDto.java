package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO события регистрации устройства в хабе.
 */
public class DeviceAddedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    @NotNull
    private DeviceTypeDto deviceType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeviceTypeDto getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeDto deviceType) {
        this.deviceType = deviceType;
    }
}
