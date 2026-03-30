package ru.yandex.practicum.kafka.telemetry.collector.events;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.kafka.telemetry.collector.hub.service.HubEventProducerService;
import ru.yandex.practicum.kafka.telemetry.collector.hub.dto.HubEventDto;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.service.SensorEventProducerService;
import ru.yandex.practicum.kafka.telemetry.collector.sensor.dto.SensorEventDto;

/**
 * REST-контроллер Collector сервиса для приёма событий от Hub Router.
 */
@RestController
@RequestMapping("/events")
@Slf4j
public class EventsController {

    private final SensorEventProducerService sensorEventProducerService;
    private final HubEventProducerService hubEventProducerService;

    public EventsController(SensorEventProducerService sensorEventProducerService,
                            HubEventProducerService hubEventProducerService) {
        this.sensorEventProducerService = sensorEventProducerService;
        this.hubEventProducerService = hubEventProducerService;
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEventDto event) {
        log.info("Received sensor event. hubId={}, type={}", event.getHubId(), event.getType());
        sensorEventProducerService.send(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEventDto event) {
        log.info("Received hub event. hubId={}, type={}", event.getHubId(), event.getType());
        hubEventProducerService.send(event);
    }
}
