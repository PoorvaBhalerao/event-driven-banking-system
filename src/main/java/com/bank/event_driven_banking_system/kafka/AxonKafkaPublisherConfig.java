package com.bank.event_driven_banking_system.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Event Bus Bridge that forwards all Axon Domain Events
 * to Kafka topics for external consumer microservices.
 */
@Configuration
@ConditionalOnProperty(name = "banking.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class AxonKafkaPublisherConfig {

    private static final Logger log = LoggerFactory.getLogger(AxonKafkaPublisherConfig.class);

    private final EventBus eventBus;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${banking.kafka.topic:banking-domain-events}")
    private String topicName;

    public AxonKafkaPublisherConfig(EventBus eventBus,
                                    KafkaTemplate<String, String> kafkaTemplate) {
        this.eventBus = eventBus;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @PostConstruct
    public void registerKafkaPublisher() {
        eventBus.subscribe((List<? extends EventMessage<?>> events) -> {
            for (EventMessage<?> event : events) {
                try {
                    String eventType = event.getPayloadType().getSimpleName();
                    String jsonPayload = objectMapper.writeValueAsString(event.getPayload());

                    log.info("📤 [Publishing Event to Kafka] Topic: {}, Type: {}, Payload: {}",
                            topicName, eventType, jsonPayload);

                    kafkaTemplate.send(topicName, event.getIdentifier(), jsonPayload)
                            .whenComplete((result, ex) -> {
                                if (ex != null) {
                                    log.warn("⚠️ [Kafka Publish Fallback] Broker unavailable locally. Event logged locally. Details: {}", ex.getMessage());
                                } else {
                                    log.info("✅ [Kafka Delivered] Offset: {}", result.getRecordMetadata().offset());
                                }
                            });

                } catch (Exception e) {
                    log.error("Error serializing Axon event for Kafka", e);
                }
            }
        });
    }
}
