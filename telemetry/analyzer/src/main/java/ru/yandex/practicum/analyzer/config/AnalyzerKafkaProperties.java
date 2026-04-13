package ru.yandex.practicum.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки Kafka для сервиса Analyzer.
 */
@ConfigurationProperties(prefix = "analyzer.kafka")
public record AnalyzerKafkaProperties(
        String bootstrapServers,
        String snapshotsGroupId,
        String hubEventsGroupId,
        Topics topics,
        long pollTimeoutMs
) {

    /**
     * Kafka топики, которые читает Analyzer.
     */
    public record Topics(
            String snapshots,
            String hubs
    ) {
    }
}
