package ru.yandex.practicum.analyzer.service;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import java.time.Instant;

/**
 * Маппер модели на gRPC запрос {@link DeviceActionRequest}.
 */
@Component
public class DeviceActionRequestMapper {

    /**
     * Создаёт {@link DeviceActionRequest} для выполнения действия устройства.
     */
    public DeviceActionRequest toGrpcRequest(String hubId, String scenarioName, ScenarioAction scenarioAction, Instant timestamp) {
        Action action = scenarioAction.getAction();

        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(scenarioAction.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType().name()));

        if (action.getValue() != null) {
            actionBuilder.setValue(action.getValue());
        }

        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionBuilder.build())
                .setTimestamp(toProtoTimestamp(timestamp))
                .build();
    }

    private Timestamp toProtoTimestamp(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
        }

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
