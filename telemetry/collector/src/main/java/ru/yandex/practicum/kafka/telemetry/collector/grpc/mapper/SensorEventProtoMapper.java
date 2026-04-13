package ru.yandex.practicum.kafka.telemetry.collector.grpc.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.ClimateSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.LightSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.MotionSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SwitchSensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.TemperatureSensorEventDto;

@Component
public class SensorEventProtoMapper {

    public SensorEventDto toDto(SensorEventProto proto) {
        if (proto == null) {
            throw new IllegalArgumentException("Sensor event proto is null");
        }

        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        SensorEventDto dto = switch (payloadCase) {
            case MOTION_SENSOR -> toMotionDto(proto);
            case TEMPERATURE_SENSOR -> toTemperatureDto(proto);
            case LIGHT_SENSOR -> toLightDto(proto);
            case CLIMATE_SENSOR -> toClimateDto(proto);
            case SWITCH_SENSOR -> toSwitchDto(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Sensor event payload is not set: " + payloadCase);
        };

        dto.setId(proto.getId());
        dto.setHubId(proto.getHubId());
        dto.setTimestamp(proto.hasTimestamp() ? ProtoTimestampMapper.toInstant(proto.getTimestamp()) : null);

        return dto;
    }

    private MotionSensorEventDto toMotionDto(SensorEventProto proto) {
        MotionSensorEventDto dto = new MotionSensorEventDto();
        dto.setType(SensorEventType.MOTION_SENSOR_EVENT);
        dto.setLinkQuality(proto.getMotionSensor().getLinkQuality());
        dto.setMotion(proto.getMotionSensor().getMotion());
        dto.setVoltage(proto.getMotionSensor().getVoltage());
        return dto;
    }

    private TemperatureSensorEventDto toTemperatureDto(SensorEventProto proto) {
        TemperatureSensorEventDto dto = new TemperatureSensorEventDto();
        dto.setType(SensorEventType.TEMPERATURE_SENSOR_EVENT);
        dto.setTemperatureC(proto.getTemperatureSensor().getTemperatureC());
        dto.setTemperatureF(proto.getTemperatureSensor().getTemperatureF());
        return dto;
    }

    private LightSensorEventDto toLightDto(SensorEventProto proto) {
        LightSensorEventDto dto = new LightSensorEventDto();
        dto.setType(SensorEventType.LIGHT_SENSOR_EVENT);
        dto.setLinkQuality(proto.getLightSensor().getLinkQuality());
        dto.setLuminosity(proto.getLightSensor().getLuminosity());
        return dto;
    }

    private ClimateSensorEventDto toClimateDto(SensorEventProto proto) {
        ClimateSensorEventDto dto = new ClimateSensorEventDto();
        dto.setType(SensorEventType.CLIMATE_SENSOR_EVENT);
        dto.setTemperatureC(proto.getClimateSensor().getTemperatureC());
        dto.setHumidity(proto.getClimateSensor().getHumidity());
        dto.setCo2Level(proto.getClimateSensor().getCo2Level());
        return dto;
    }

    private SwitchSensorEventDto toSwitchDto(SensorEventProto proto) {
        SwitchSensorEventDto dto = new SwitchSensorEventDto();
        dto.setType(SensorEventType.SWITCH_SENSOR_EVENT);
        dto.setState(proto.getSwitchSensor().getState());
        return dto;
    }
}
