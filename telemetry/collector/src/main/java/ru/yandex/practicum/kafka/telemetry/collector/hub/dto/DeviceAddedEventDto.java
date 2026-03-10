package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO события регистрации устройства в хабе.
 */
@Data
public class DeviceAddedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    @NotNull
    private DeviceTypeDto deviceType;
}
