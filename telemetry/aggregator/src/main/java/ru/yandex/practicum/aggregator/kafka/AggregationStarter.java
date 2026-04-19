package ru.yandex.practicum.aggregator.kafka;

import deserializer.SensorEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.snapshot.SensorsSnapshotAggregator;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import serialization.AvroBinarySerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Запускает процесс агрегации: читает события датчиков из Kafka и публикует обновлённые снапшоты.
 */
@Component
public class AggregationStarter {

    private static final Logger log = LoggerFactory.getLogger(AggregationStarter.class);

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;

    private final SensorsSnapshotAggregator snapshotAggregator;

    private final String sensorsTopic;
    private final String snapshotsTopic;
    private final long pollTimeoutMs;

    public AggregationStarter(
            SensorsSnapshotAggregator snapshotAggregator,
            @Value("${aggregator.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${aggregator.kafka.group-id}") String groupId,
            @Value("${aggregator.kafka.topics.sensors}") String sensorsTopic,
            @Value("${aggregator.kafka.topics.snapshots}") String snapshotsTopic,
            @Value("${aggregator.kafka.poll-timeout-ms}") long pollTimeoutMs
    ) {
        this.snapshotAggregator = snapshotAggregator;
        this.sensorsTopic = sensorsTopic;
        this.snapshotsTopic = snapshotsTopic;
        this.pollTimeoutMs = pollTimeoutMs;

        this.consumer = new KafkaConsumer<>(consumerProps(bootstrapServers, groupId));
        this.producer = new KafkaProducer<>(producerProps(bootstrapServers));
    }

    /**
     * Запускает poll-loop Kafka consumer.
     */
    public void start() {
        try {
            consumer.subscribe(List.of(sensorsTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    snapshotAggregator.updateState(record.value())
                            .ifPresent(snapshot -> producer.send(new ProducerRecord<>(snapshotsTopic, snapshot.getHubId(), snapshot)));
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("Ошибка во время завершения работы", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private Properties consumerProps(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    private Properties producerProps(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroBinarySerializer.class.getName());
        return props;
    }
}
