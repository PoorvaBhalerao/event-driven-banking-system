package com.bank.event_driven_banking_system.query.controller;

import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Event Replay Controller demonstrating CQRS & Event Sourcing state rebuild.
 * Allows administrators to wipe read-model databases and replay historical events
 * from offset 0 (beginning of time).
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AccountReplayController {

    private static final Logger log = LoggerFactory.getLogger(AccountReplayController.class);

    private final AccountRepository accountRepository;
    private final EventProcessingConfiguration eventProcessingConfiguration;

    public AccountReplayController(AccountRepository accountRepository,
                                   EventProcessingConfiguration eventProcessingConfiguration) {
        this.accountRepository = accountRepository;
        this.eventProcessingConfiguration = eventProcessingConfiguration;
    }

    @PostMapping("/replay-events")
    public ResponseEntity<Map<String, String>> replayEvents() {
        log.info("🔄 [Event Replay Triggered] Wiping read-model database...");

        // 1. Wipe query read model table
        accountRepository.deleteAll();

        // 2. Reset tracking event processor token to 0 if tracking processor is active
        String processorName = "com.bank.event_driven_banking_system.query.projection";
        eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class)
                .ifPresent(processor -> {
                    log.info("Resetting tracking processor '{}' to head of stream...", processorName);
                    processor.shutDown();
                    processor.resetTokens();
                    processor.start();
                });

        log.info("✅ [Event Replay Complete] Read-model successfully triggered for rebuild.");
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Read-model database wiped. Tracking tokens reset to position 0 for event replay."
        ));
    }
}
