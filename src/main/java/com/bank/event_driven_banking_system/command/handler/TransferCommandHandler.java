package com.bank.event_driven_banking_system.command.handler;

import com.bank.event_driven_banking_system.command.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.command.events.TransferInitiatedEvent;
import com.bank.event_driven_banking_system.command.idempotency.entity.IdempotencyRecord;
import com.bank.event_driven_banking_system.command.idempotency.repository.IdempotencyRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class TransferCommandHandler {

    private final EventGateway eventGateway;
    private final IdempotencyRepository idempotencyRepository;

    public TransferCommandHandler(EventGateway eventGateway, IdempotencyRepository idempotencyRepository) {
        this.eventGateway = eventGateway;
        this.idempotencyRepository =idempotencyRepository;
    }

    @CommandHandler
    public String handle(TransferMoneyCommand command) {

        // 1. Validate idempotency key
        if (command.getIdempotencyKey() == null || command.getIdempotencyKey().isBlank())
        {
            throw new IllegalArgumentException( "Idempotency-Key is required." );
        }

        // 2.Validate transfer amount
        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        // 3.Validate source and destination accounts
        if (command.getSourceAccountId() == null
                || command.getDestinationAccountId() == null) {

            throw new IllegalArgumentException(
                    "Source and destination accounts are required"
            );
        }

        // 4.Source and destination must be different
        if (command.getSourceAccountId()
                .equalsIgnoreCase(command.getDestinationAccountId())) {

            throw new IllegalArgumentException(
                    "Source and destination accounts must be different"
            );
        }

        // 5. Check whether this request was already processed
        var existingRecord = idempotencyRepository.findByIdempotencyKey( command.getIdempotencyKey() );

        if (existingRecord.isPresent()) {
            return existingRecord.get().getTransferId();
        }

        // 6. Create idempotency record
        IdempotencyRecord record = new IdempotencyRecord( command.getIdempotencyKey(),
                command.getTransferId(), "PROCESSING" );

        try {
            idempotencyRepository.saveAndFlush(record);
        }
        catch (DataIntegrityViolationException ex)
        { // Another request with the same idempotency key
            // was processed at the same time.
            return idempotencyRepository
                    .findByIdempotencyKey(command.getIdempotencyKey())
                    .orElseThrow(() ->
                            new IllegalStateException( "Unable to resolve idempotency record."
                            ))
                    .getTransferId(); }

        // 7. Start the transfer Saga
        eventGateway.publish(
                new TransferInitiatedEvent(
                        command.getTransferId(),
                        command.getSourceAccountId(),
                        command.getDestinationAccountId(),
                        command.getAmount() ) );

        // 8. Return transfer ID
        return command.getTransferId();

    }
}
