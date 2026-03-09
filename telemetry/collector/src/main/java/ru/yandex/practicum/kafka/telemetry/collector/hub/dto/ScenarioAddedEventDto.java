package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO события добавления сценария.
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScenarioConditionDto> getConditions() {
        return conditions;
    }

    public void setConditions(List<ScenarioConditionDto> conditions) {
        this.conditions = conditions;
    }

    public List<DeviceActionDto> getActions() {
        return actions;
    }

    public void setActions(List<DeviceActionDto> actions) {
        this.actions = actions;
    }
}
