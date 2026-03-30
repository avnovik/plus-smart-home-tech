package ru.yandex.practicum.kafka.telemetry.collector.hub.dto;

/**
 * Тип события хаба.
 */
public enum HubEventType {
    DEVICE_ADDED,
    DEVICE_REMOVED,
    SCENARIO_ADDED,
    SCENARIO_REMOVED
}
