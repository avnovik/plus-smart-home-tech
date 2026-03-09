package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO события удаления устройства из хаба.
 */
public class DeviceRemovedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
