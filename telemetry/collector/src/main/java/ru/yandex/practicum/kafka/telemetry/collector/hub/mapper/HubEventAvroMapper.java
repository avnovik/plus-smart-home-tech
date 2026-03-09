package ru.yandex.practicum.kafka.telemetry.collector.hub.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ActionTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ConditionOperationDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ConditionTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceActionDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceAddedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceRemovedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.HubEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioAddedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioConditionDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioRemovedEventDto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Instant;
import java.util.List;

/**
 * Маппер DTO события хаба в Avro.
 */
@Component
public class HubEventAvroMapper {

    public HubEventAvro toAvro(HubEventDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Hub event is null");
        }

        Instant timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();

        return toAvro(dto, timestamp);
    }

    public HubEventAvro toAvro(HubEventDto dto, Instant timestamp) {
        if (dto == null) {
            throw new IllegalArgumentException("Hub event is null");
        }

        if (timestamp == null) {
            timestamp = Instant.now();
        }

        return HubEventAvro.newBuilder()
                .setHubId(dto.getHubId())
                .setTimestamp(timestamp)
                .setPayload(toPayload(dto))
                .build();
    }

    private Object toPayload(HubEventDto dto) {
        if (dto instanceof DeviceAddedEventDto added) {
            return new DeviceAddedEventAvro(added.getId(), toDeviceType(added.getDeviceType()));
        }

        if (dto instanceof DeviceRemovedEventDto removed) {
            return new DeviceRemovedEventAvro(removed.getId());
        }

        if (dto instanceof ScenarioAddedEventDto scenarioAdded) {
            List<ScenarioConditionAvro> conditions = scenarioAdded.getConditions().stream()
                    .map(this::toScenarioCondition)
                    .toList();

            List<DeviceActionAvro> actions = scenarioAdded.getActions().stream()
                    .map(this::toDeviceAction)
                    .toList();

            return new ScenarioAddedEventAvro(scenarioAdded.getName(), conditions, actions);
        }

        if (dto instanceof ScenarioRemovedEventDto scenarioRemoved) {
            return new ScenarioRemovedEventAvro(scenarioRemoved.getName());
        }

        throw new IllegalArgumentException("Unknown hub event dto: " + dto.getClass().getName());
    }

    private ScenarioConditionAvro toScenarioCondition(ScenarioConditionDto dto) {
        return new ScenarioConditionAvro(
                dto.getSensorId(),
                toConditionType(dto.getType()),
                toConditionOperation(dto.getOperation()),
                dto.getValue()
        );
    }

    private DeviceActionAvro toDeviceAction(DeviceActionDto dto) {
        return new DeviceActionAvro(
                dto.getSensorId(),
                toActionType(dto.getType()),
                dto.getValue()
        );
    }

    private DeviceTypeAvro toDeviceType(DeviceTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return DeviceTypeAvro.valueOf(dto.name());
    }

    private ConditionTypeAvro toConditionType(ConditionTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return ConditionTypeAvro.valueOf(dto.name());
    }

    private ConditionOperationAvro toConditionOperation(ConditionOperationDto dto) {
        if (dto == null) {
            return null;
        }
        return ConditionOperationAvro.valueOf(dto.name());
    }

    private ActionTypeAvro toActionType(ActionTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return ActionTypeAvro.valueOf(dto.name());
    }
}
