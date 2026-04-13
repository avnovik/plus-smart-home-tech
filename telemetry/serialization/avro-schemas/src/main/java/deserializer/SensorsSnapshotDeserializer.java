package deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Kafka десериализатор для Avro сообщения {@link SensorsSnapshotAvro}.
 */
public class SensorsSnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SensorsSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
