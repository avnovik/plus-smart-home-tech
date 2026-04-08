package ru.yandex.practicum.kafka.telemetry.collector.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.collector.grpc.mapper.HubEventProtoMapper;
import ru.yandex.practicum.kafka.telemetry.collector.grpc.mapper.SensorEventProtoMapper;
import ru.yandex.practicum.kafka.telemetry.collector.hub.service.HubEventProducerService;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.service.SensorEventProducerService;

@GrpcService
@Slf4j
public class CollectorGrpcService extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final SensorEventProducerService sensorEventProducerService;
    private final HubEventProducerService hubEventProducerService;
    private final SensorEventProtoMapper sensorEventProtoMapper;
    private final HubEventProtoMapper hubEventProtoMapper;

    public CollectorGrpcService(
            SensorEventProducerService sensorEventProducerService,
            HubEventProducerService hubEventProducerService,
            SensorEventProtoMapper sensorEventProtoMapper,
            HubEventProtoMapper hubEventProtoMapper
    ) {
        this.sensorEventProducerService = sensorEventProducerService;
        this.hubEventProducerService = hubEventProducerService;
        this.sensorEventProtoMapper = sensorEventProtoMapper;
        this.hubEventProtoMapper = hubEventProtoMapper;
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received gRPC sensor event. hubId={}, payloadCase={}", request.getHubId(), request.getPayloadCase());

            sensorEventProducerService.send(sensorEventProtoMapper.toDto(request));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received gRPC hub event. hubId={}, payloadCase={}", request.getHubId(), request.getPayloadCase());

            hubEventProducerService.send(hubEventProtoMapper.toDto(request));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
