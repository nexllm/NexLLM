package io.github.nexllm.infra.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Override
    public <T extends SpecificRecord> void publish(String topic, String key, T event) {
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex == null) {
                // Log success
                log.info("Successfully published event: {}", event);
            } else {
                // Log failure and metrics
                log.error("Failed to publish event: {}", event, ex);
            }
        });
    }
}
