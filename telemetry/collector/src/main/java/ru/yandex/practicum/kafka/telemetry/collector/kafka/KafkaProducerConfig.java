package ru.yandex.practicum.kafka.telemetry.collector.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import serialization.AvroBinarySerializer;

import java.util.Properties;

/**
 * Конфигурация Kafka producer.
 */
@Configuration
public class KafkaProducerConfig {

    @Bean(destroyMethod = "close")
    public KafkaProducer<String, SpecificRecord> kafkaProducer(
            @Value("${collector.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroBinarySerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}
