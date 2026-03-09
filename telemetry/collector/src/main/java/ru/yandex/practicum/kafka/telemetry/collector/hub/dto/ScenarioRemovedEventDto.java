package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO события удаления сценария.
 */
public class ScenarioRemovedEventDto extends HubEventDto {

    @NotBlank
    @Size(min = 3)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
