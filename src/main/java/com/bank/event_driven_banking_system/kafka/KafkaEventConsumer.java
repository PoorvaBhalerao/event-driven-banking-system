package com.bank.event_driven_banking_system.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * External Kafka Event Listener simulating decoupled consumer services
 * (e.g., Notification Service, Fraud Detection, External Analytics).
 */
@Component
@ConditionalOnProperty(name = "banking.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = "${banking.kafka.topic:banking-domain-events}", groupId = "${spring.kafka.consumer.group-id:banking-system-group}")
    public void consumeDomainEvent(String message) {
        log.info("📬 [Kafka Consumer Received Event]: {}", message);
        receivedMessages.add(message);
    }

    public List<String> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }

    public void clearMessages() {
        receivedMessages.clear();
    }
}
