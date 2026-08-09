package com.bank.event_driven_banking_system.command.handler;

import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.command.events.TransferCompletedEvent;
import com.bank.event_driven_banking_system.command.events.TransferFailedEvent;
import com.bank.event_driven_banking_system.command.events.TransferInitiatedEvent;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

@Component
public class TransferCommandHandler {

    private final CommandGateway commandGateway;
    private final EventGateway eventGateway;
    private final AccountRepository accountRepository;

    public TransferCommandHandler(CommandGateway commandGateway,
                                  EventGateway eventGateway,
                                  AccountRepository accountRepository) {
        this.commandGateway = commandGateway;
        this.eventGateway = eventGateway;
        this.accountRepository = accountRepository;
    }

    @CommandHandler
    public String handle(TransferMoneyCommand command) {

        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        if (command.getSourceAccountId() == null || command.getDestinationAccountId() == null
                || command.getSourceAccountId().equalsIgnoreCase(command.getDestinationAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different.");
        }

        if (!accountRepository.existsById(command.getSourceAccountId())) {
            throw new IllegalArgumentException("Source account not found.");
        }

        if (!accountRepository.existsById(command.getDestinationAccountId())) {
            throw new IllegalArgumentException("Destination account not found.");
        }

        // Publish TransferInitiatedEvent
        eventGateway.publish(new TransferInitiatedEvent(
                command.getTransferId(),
                command.getSourceAccountId(),
                command.getDestinationAccountId(),
                command.getAmount()
        ));

        try {
            // Debit source account via WithdrawMoneyCommand
            commandGateway.sendAndWait(new WithdrawMoneyCommand(
                    command.getSourceAccountId(),
                    command.getAmount()
            ));

            // Credit destination account via DepositMoneyCommand
            commandGateway.sendAndWait(new DepositMoneyCommand(
                    command.getDestinationAccountId(),
                    command.getAmount()
            ));

            // Publish TransferCompletedEvent
            eventGateway.publish(new TransferCompletedEvent(
                    command.getTransferId(),
                    command.getSourceAccountId(),
                    command.getDestinationAccountId(),
                    command.getAmount()
            ));

            return command.getTransferId();

        } catch (Exception ex) {
            // Publish TransferFailedEvent on failure
            eventGateway.publish(new TransferFailedEvent(
                    command.getTransferId(),
                    command.getSourceAccountId(),
                    command.getDestinationAccountId(),
                    command.getAmount(),
                    ex.getMessage()
            ));
            throw ex;
        }
    }
}
