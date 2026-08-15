package com.bank.event_driven_banking_system.config.upcaster;

import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Event Upcaster for schema evolution.
 * Upgrades legacy V1 AccountOpenedEvent to V2 by injecting a default currency ("USD")
 * if the currency field is absent in historical event payloads.
 */
@Component
public class AccountOpenedEventUpcaster extends SingleEventUpcaster {

    private static final String TARGET_TYPE_NEW = "com.bank.event_driven_banking_system.core.events.AccountOpenedEvent";
    private static final String TARGET_TYPE_LEGACY = "com.bank.event_driven_banking_system.command.events.AccountOpenedEvent";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        SerializedType type = intermediateRepresentation.getType();
        boolean isTargetType = TARGET_TYPE_NEW.equals(type.getName()) || TARGET_TYPE_LEGACY.equals(type.getName());
        return isTargetType && (type.getRevision() == null || "1.0".equals(type.getRevision()));
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return intermediateRepresentation.upcastPayload(
                new SimpleSerializedType(TARGET_TYPE_NEW, "2.0"),
                byte[].class,
                bytes -> {
                    try {
                        ObjectNode node = (ObjectNode) objectMapper.readTree(bytes);
                        if (!node.has("currency")) {
                            node.put("currency", "USD");
                        }
                        return objectMapper.writeValueAsBytes(node);
                    } catch (Exception e) {
                        return bytes;
                    }
                }
        );
    }
}
