package io.github.nexllm.infra.kafka.publisher;

import org.apache.avro.specific.SpecificRecord;

public interface EventPublisher {

    <T extends SpecificRecord> void publish(String topic, String key, T event);
}

