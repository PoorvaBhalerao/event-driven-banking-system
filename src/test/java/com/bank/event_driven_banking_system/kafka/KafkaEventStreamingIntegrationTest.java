package com.bank.event_driven_banking_system.kafka;

import com.bank.event_driven_banking_system.command.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"banking-domain-events-test"})
@org.springframework.test.context.TestPropertySource(properties = {
        "banking.kafka.enabled=true",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
@Import(KafkaEventStreamingIntegrationTest.AxonTestConfig.class)
public class KafkaEventStreamingIntegrationTest {

    @TestConfiguration
    static class AxonTestConfig {
        @Autowired
        public void configure(EventProcessingConfigurer configurer) {
            configurer.usingSubscribingEventProcessors();
        }
    }

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private KafkaEventConsumer kafkaEventConsumer;

    @Test
    public void testEventStreamingToKafka() {
        kafkaEventConsumer.clearMessages();

        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Kafka Test User", 500.0));
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, 200.0));

        // Verify that events are captured by the Kafka consumer
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertFalse(kafkaEventConsumer.getReceivedMessages().isEmpty(), "Kafka consumer should receive streamed events");
        });
    }
}
