package deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

/**
 * Kafka десериализатор для Avro сообщения {@link SensorEventAvro}.
 */
public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
