package io.github.nexllm.infra.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.github.nexllm.infra.kafka.publisher.EventPublisher;
import io.github.nexllm.infra.kafka.publisher.KafkaEventPublisher;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private final KafkaProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, SpecificRecord> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        config.put("schema.registry.url", properties.schemaRegistryUrl());
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(ProducerFactory<String, SpecificRecord> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerFactory<String, SpecificRecord> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        config.put("schema.registry.url", properties.schemaRegistryUrl());
        config.put("specific.avro.reader", "true");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
        KafkaTemplate<String, SpecificRecord> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate);
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        // retry policy
        FixedBackOff backOff = new FixedBackOff(2000L, 3L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        handler.addNotRetryableExceptions(org.apache.avro.AvroRuntimeException.class);

        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Kafka consume failed, retrying attempt {} for topic: {}, partition: {}, offset: {}. Error: {}",
                deliveryAttempt, record.topic(), record.partition(), record.offset(), ex.getMessage());
        });
        return handler;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(
        ConsumerFactory<String, SpecificRecord> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        //todo retry policy
        return factory;
    }

    @Bean
    public EventPublisher eventPublisher(KafkaTemplate<String, SpecificRecord> kafkaTemplate) {
        return new KafkaEventPublisher(kafkaTemplate);
    }
}
