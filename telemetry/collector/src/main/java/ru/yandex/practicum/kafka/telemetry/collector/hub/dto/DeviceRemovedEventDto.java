package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO события удаления устройства из хаба.
 */
@Data
public class DeviceRemovedEventDto extends HubEventDto {

    @NotBlank
    private String id;
}
