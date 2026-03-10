package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO события добавления сценария.
 */
@Data
public class ScenarioAddedEventDto extends HubEventDto {

    @NotBlank
    @Size(min = 3)
    private String name;

    @NotNull
    @Size(min = 1)
    private List<@Valid ScenarioConditionDto> conditions;

    @NotNull
    @Size(min = 1)
    private List<@Valid DeviceActionDto> actions;
}
