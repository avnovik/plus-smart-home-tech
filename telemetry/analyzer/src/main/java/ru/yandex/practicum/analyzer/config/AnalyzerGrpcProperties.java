package ru.yandex.practicum.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки gRPC для сервиса Analyzer.
 */
@ConfigurationProperties(prefix = "analyzer.grpc")
public record AnalyzerGrpcProperties(
        HubRouter hubRouter
) {

    public record HubRouter(
            String address
    ) {
    }
}
