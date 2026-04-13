package deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

/**
 * Kafka десериализатор для Avro сообщения {@link HubEventAvro}.
 */
public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {

    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
