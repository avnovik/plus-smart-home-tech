package ru.yandex.practicum.kafka.telemetry.collector.hub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.collector.kafka.KafkaAvroEventSender;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.HubEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.hub.mapper.HubEventAvroMapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

/**
 * Отправка событий хаба в Kafka.
 */
@Service
public class HubEventProducerService {

    private final KafkaAvroEventSender sender;
    private final HubEventAvroMapper mapper;
    private final String topic;

    public HubEventProducerService(
            KafkaAvroEventSender sender,
            HubEventAvroMapper mapper,
            @Value("${collector.kafka.topics.hubs}") String topic
    ) {
        this.sender = sender;
        this.mapper = mapper;
        this.topic = topic;
    }

    public void send(HubEventDto dto) {
        Instant timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now();
        HubEventAvro event = mapper.toAvro(dto, timestamp);
        sender.send(topic, dto.getHubId(), timestamp, event, "Hub");
    }
}
