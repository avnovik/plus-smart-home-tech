package ru.yandex.practicum.kafka.telemetry.collector.sensor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.collector.kafka.KafkaAvroEventSender;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SensorEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

/**
 * Отправка событий датчиков в Kafka.
 */
@Service
@RequiredArgsConstructor
public class SensorEventProducerService {

    private final KafkaAvroEventSender sender;
    private final SensorEventAvroMapper mapper;

    @Value("${collector.kafka.topics.sensors}")
    private String topic;

    public void send(SensorEventDto dto) {
        Instant timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();
        SensorEventAvro event = mapper.toAvro(dto, timestamp);
        sender.send(topic, dto.getHubId(), timestamp, event, "Sensor");
    }
}
