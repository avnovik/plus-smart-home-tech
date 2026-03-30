package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO события удаления сценария.
 */
@Data
public class ScenarioRemovedEventDto extends HubEventDto {

    @NotBlank
    @Size(min = 3)
    private String name;
}
