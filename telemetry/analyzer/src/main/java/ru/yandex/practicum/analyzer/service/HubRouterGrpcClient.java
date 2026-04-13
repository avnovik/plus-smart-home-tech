package ru.yandex.practicum.analyzer.service;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

/**
 * gRPC клиент для Hub Router.
 */
@Slf4j
@Service
public class HubRouterGrpcClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterGrpcClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    /**
     * Отправляет команду на выполнение действия устройству через Hub Router.
     */
    public void handleDeviceAction(DeviceActionRequest request) {
        log.info("Отправляем gRPC команду: hubId={}, scenarioName={}, sensorId={}, actionType={} value={} ",
                request.getHubId(),
                request.getScenarioName(),
                request.getAction().getSensorId(),
                request.getAction().getType(),
                request.getAction().getValue()
        );

        try {
            hubRouterClient.handleDeviceAction(request);
        } catch (StatusRuntimeException e) {
            log.error("gRPC вызов Hub Router не выполнен: hubId={}, scenarioName={}, sensorId={}, status={}",
                    request.getHubId(),
                    request.getScenarioName(),
                    request.getAction().getSensorId(),
                    e.getStatus(),
                    e);
        }
    }

    /**
     * Проверка, что запрос к gRPC может быть отправлен.
     */
    public boolean isValid(DeviceActionRequest request) {
        return !request.getHubId().isBlank()
                && !request.getScenarioName().isBlank()
                && !request.getAction().getSensorId().isBlank();
    }
}