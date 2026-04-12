package ru.yandex.practicum.analyzer.processor;

import deserializer.HubEventDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.AnalyzerKafkaProperties;
import ru.yandex.practicum.analyzer.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Обработчик событий хабов.
 */
@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final String bootstrapServers;
    private final String groupId;
    private final String topic;
    private final long pollTimeoutMs;
    private final HubEventService hubEventService;

    public HubEventProcessor(AnalyzerKafkaProperties kafkaProperties, HubEventService hubEventService) {
        this.bootstrapServers = kafkaProperties.bootstrapServers();
        this.groupId = kafkaProperties.hubEventsGroupId();
        this.topic = kafkaProperties.topics().hubs();
        this.pollTimeoutMs = kafkaProperties.pollTimeoutMs();
        this.hubEventService = hubEventService;
    }

    @Override
    public void run() {
        try (KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(consumerProps(bootstrapServers, groupId))) {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        handleRecord(record);
                    } catch (Exception e) {
                        HubEventAvro event = record.value();
                        log.error("Ошибка обработки события хаба: hubId={}, partition={}, offset={}",
                                event == null ? null : event.getHubId(),
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
            log.error("Ошибка во время обработки событий хабов", e);
        }
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro event = record.value();
        if (event == null) {
            return;
        }

        log.info("Получено событие хаба: hubId={}, timestamp={}, partition={}, offset={}",
                event.getHubId(),
                event.getTimestamp(),
                record.partition(),
                record.offset()
        );
        log.debug("Payload event: {}", event.getPayload());

        hubEventService.handle(event);
    }

    private Properties consumerProps(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
