package ru.yandex.practicum.kafka.telemetry.collector.grpc.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ActionTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ConditionOperationDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ConditionTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceActionDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceAddedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceRemovedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.DeviceTypeDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.HubEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.HubEventType;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioAddedEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioConditionDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.ScenarioRemovedEventDto;

import java.util.List;

@Component
public class HubEventProtoMapper {

    public HubEventDto toDto(HubEventProto proto) {
        if (proto == null) {
            throw new IllegalArgumentException("Hub event proto is null");
        }

        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        HubEventDto dto = switch (payloadCase) {
            case DEVICE_ADDED -> toDeviceAddedDto(proto);
            case DEVICE_REMOVED -> toDeviceRemovedDto(proto);
            case SCENARIO_ADDED -> toScenarioAddedDto(proto);
            case SCENARIO_REMOVED -> toScenarioRemovedDto(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Hub event payload is not set: " + payloadCase);
        };

        dto.setHubId(proto.getHubId());
        dto.setTimestamp(proto.hasTimestamp() ? ProtoTimestampMapper.toInstant(proto.getTimestamp()) : null);

        return dto;
    }

    private DeviceAddedEventDto toDeviceAddedDto(HubEventProto proto) {
        DeviceAddedEventDto dto = new DeviceAddedEventDto();
        dto.setType(HubEventType.DEVICE_ADDED);
        dto.setId(proto.getDeviceAdded().getId());
        dto.setDeviceType(DeviceTypeDto.valueOf(proto.getDeviceAdded().getType().name()));
        return dto;
    }

    private DeviceRemovedEventDto toDeviceRemovedDto(HubEventProto proto) {
        DeviceRemovedEventDto dto = new DeviceRemovedEventDto();
        dto.setType(HubEventType.DEVICE_REMOVED);
        dto.setId(proto.getDeviceRemoved().getId());
        return dto;
    }

    private ScenarioAddedEventDto toScenarioAddedDto(HubEventProto proto) {
        ScenarioAddedEventDto dto = new ScenarioAddedEventDto();
        dto.setType(HubEventType.SCENARIO_ADDED);
        dto.setName(proto.getScenarioAdded().getName());

        List<ScenarioConditionDto> conditions = proto.getScenarioAdded().getConditionList().stream()
                .map(this::toScenarioConditionDto)
                .toList();

        List<DeviceActionDto> actions = proto.getScenarioAdded().getActionList().stream()
                .map(this::toDeviceActionDto)
                .toList();

        dto.setConditions(conditions);
        dto.setActions(actions);
        return dto;
    }

    private ScenarioRemovedEventDto toScenarioRemovedDto(HubEventProto proto) {
        ScenarioRemovedEventDto dto = new ScenarioRemovedEventDto();
        dto.setType(HubEventType.SCENARIO_REMOVED);
        dto.setName(proto.getScenarioRemoved().getName());
        return dto;
    }

    private ScenarioConditionDto toScenarioConditionDto(ScenarioConditionProto proto) {
        ScenarioConditionDto dto = new ScenarioConditionDto();
        dto.setSensorId(proto.getSensorId());
        dto.setType(ConditionTypeDto.valueOf(proto.getType().name()));
        dto.setOperation(ConditionOperationDto.valueOf(proto.getOperation().name()));

        ScenarioConditionProto.ValueCase valueCase = proto.getValueCase();
        Integer value = switch (valueCase) {
            case BOOL_VALUE -> proto.getBoolValue() ? 1 : 0;
            case INT_VALUE -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
        };

        dto.setValue(value);
        return dto;
    }

    private DeviceActionDto toDeviceActionDto(ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto proto) {
        DeviceActionDto dto = new DeviceActionDto();
        dto.setSensorId(proto.getSensorId());
        dto.setType(ActionTypeDto.valueOf(proto.getType().name()));
        dto.setValue(proto.hasValue() ? proto.getValue() : null);
        return dto;
    }
}
