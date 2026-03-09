package ru.yandex.practicum.kafka.telemetry.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import serialization.AvroBinarySerializer;

import java.time.Instant;

/**
 * Общая отправка Avro-сообщений в Kafka.
 */
@Component
@Slf4j
public class KafkaAvroEventSender {

    private final KafkaProducer<String, byte[]> producer;
    private final AvroBinarySerializer serializer;

    public KafkaAvroEventSender(KafkaProducer<String, byte[]> producer, AvroBinarySerializer serializer) {
        this.producer = producer;
        this.serializer = serializer;
    }

    public void send(String topic, String hubId, Instant timestamp, SpecificRecord avro, String eventType) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                hubId,
                serializer.toBytes(avro)
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.warn("Failed to send {} event to Kafka. topic={}, hubId={}", eventType, topic, hubId, exception);
                return;
            }

            log.info("{} event sent to Kafka. topic={}, partition={}, offset={}, hubId={}",
                    eventType, metadata.topic(), metadata.partition(), metadata.offset(), hubId);
        });
    }
}
