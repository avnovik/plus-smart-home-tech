package ru.yandex.practicum.analyzer.processor;

import deserializer.SensorsSnapshotDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.AnalyzerKafkaProperties;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.service.DeviceActionRequestMapper;
import ru.yandex.practicum.analyzer.service.HubRouterGrpcClient;
import ru.yandex.practicum.analyzer.service.SnapshotScenarioEvaluator;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Обработчик снапшотов состояния датчиков.
 */
@Slf4j
@Component
public class SnapshotProcessor {

    private final String bootstrapServers;
    private final String groupId;
    private final String topic;
    private final long pollTimeoutMs;
    private final SnapshotScenarioEvaluator evaluator;
    private final DeviceActionRequestMapper requestMapper;
    private final HubRouterGrpcClient hubRouterGrpcClient;

    public SnapshotProcessor(AnalyzerKafkaProperties kafkaProperties,
                             SnapshotScenarioEvaluator evaluator,
                             DeviceActionRequestMapper requestMapper,
                             HubRouterGrpcClient hubRouterGrpcClient) {
        this.bootstrapServers = kafkaProperties.bootstrapServers();
        this.groupId = kafkaProperties.snapshotsGroupId();
        this.topic = kafkaProperties.topics().snapshots();
        this.pollTimeoutMs = kafkaProperties.pollTimeoutMs();
        this.evaluator = evaluator;
        this.requestMapper = requestMapper;
        this.hubRouterGrpcClient = hubRouterGrpcClient;
    }

    /**
     * Запускает poll-loop Kafka consumer.
     */
    public void start() {
        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(consumerProps(bootstrapServers, groupId))) {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        handleRecord(record);
                    } catch (Exception e) {
                        SensorsSnapshotAvro snapshot = record.value();
                        log.error("Ошибка обработки снапшота: hubId={}, partition={}, offset={}",
                                snapshot == null ? null : snapshot.getHubId(),
                                record.partition(),
                                record.offset(),
                                e);
                    }
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro snapshot = record.value();
        if (snapshot == null) {
            return;
        }

        log.info("Получен снапшот: hubId={}, timestamp={}, sensorsCount={}, partition={}, offset={}",
                snapshot.getHubId(),
                snapshot.getTimestamp(),
                snapshot.getSensorsState() == null ? 0 : snapshot.getSensorsState().size(),
                record.partition(),
                record.offset()
        );

        List<ScenarioAction> actionsToExecute = evaluator.evaluate(snapshot);
        if (actionsToExecute.isEmpty()) {
            log.info("Для снапшота действий нет: hubId={}, timestamp={}", snapshot.getHubId(), snapshot.getTimestamp());
            return;
        }

        for (ScenarioAction scenarioAction : actionsToExecute) {
            log.debug("Action к выполнению: hubId={}, scenarioName={}, sensorId={}, actionType={}, value={}",
                    snapshot.getHubId(),
                    scenarioAction.getScenario().getName(),
                    scenarioAction.getSensor().getId(),
                    scenarioAction.getAction().getType(),
                    scenarioAction.getAction().getValue()
            );

            if (requestMapper != null && hubRouterGrpcClient != null) {
                DeviceActionRequest request = requestMapper.toGrpcRequest(
                        snapshot.getHubId(),
                        scenarioAction.getScenario().getName(),
                        scenarioAction,
                        snapshot.getTimestamp()
                );

                if (hubRouterGrpcClient.isValid(request)) {
                    hubRouterGrpcClient.handleDeviceAction(request);
                } else {
                    log.warn("Некорректный DeviceActionRequest, пропускаем: {}", request);
                }
            }
        }
    }

    private Properties consumerProps(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
