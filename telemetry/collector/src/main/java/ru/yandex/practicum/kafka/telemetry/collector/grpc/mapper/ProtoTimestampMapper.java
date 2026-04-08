package ru.yandex.practicum.kafka.telemetry.collector.grpc.mapper;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public final class ProtoTimestampMapper {

    private ProtoTimestampMapper() {
    }

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
