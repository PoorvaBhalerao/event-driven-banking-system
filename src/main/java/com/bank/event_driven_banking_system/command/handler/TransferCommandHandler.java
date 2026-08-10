package com.bank.event_driven_banking_system.command.handler;

import com.bank.event_driven_banking_system.command.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.command.events.TransferInitiatedEvent;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

@Component
public class TransferCommandHandler {

    private final EventGateway eventGateway;

    public TransferCommandHandler(EventGateway eventGateway) {
        this.eventGateway = eventGateway;
    }

    public void handle(TransferMoneyCommand command) {

        // Validate transfer amount
        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        // Validate source and destination accounts
        if (command.getSourceAccountId() == null
                || command.getDestinationAccountId() == null) {

            throw new IllegalArgumentException(
                    "Source and destination accounts are required"
            );
        }

        // Source and destination must be different
        if (command.getSourceAccountId()
                .equalsIgnoreCase(command.getDestinationAccountId())) {

            throw new IllegalArgumentException(
                    "Source and destination accounts must be different"
            );
        }

        // Start the transfer workflow.
        eventGateway.publish(
                new TransferInitiatedEvent(
                        command.getTransferId(),
                        command.getSourceAccountId(),
                        command.getDestinationAccountId(),
                        command.getAmount()
                )
        );
    }
}
