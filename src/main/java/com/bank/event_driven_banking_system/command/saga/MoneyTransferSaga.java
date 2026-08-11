package com.bank.event_driven_banking_system.command.saga;
import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.command.events.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import com.bank.event_driven_banking_system.command.events.CompensationFailedEvent;
import com.bank.event_driven_banking_system.command.idempotency.repository.IdempotencyRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Saga                                   //This class is an Axon Saga// Its job is to coordinate a long-running business transaction
@ProcessingGroup("money-transfer-saga")
public class MoneyTransferSaga
{
    // CommandGateway -saga sends commands to aggregate
    // transient - Don't persist this injected infrastructure object as Saga state

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient EventGateway eventGateway;

    @Autowired
    private transient IdempotencyRepository idempotencyRepository;


    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private double amount;

    private boolean compensationInProgress;
    private String failureReason;

    @StartSaga
    @SagaEventHandler(associationProperty = "transferId")
    public void handle(TransferInitiatedEvent event) {

        this.transferId = event.getTransferId();
        this.sourceAccountId = event.getSourceAccountId();
        this.destinationAccountId = event.getDestinationAccountId();
        this.amount = event.getAmount();

        try {
            commandGateway.sendAndWait(new WithdrawMoneyCommand(
                            sourceAccountId,
                            amount,
                            transferId
                    ));
        }
        catch (Exception ex)
        {
            eventGateway.publish(
                    new TransferFailedEvent(
                            transferId,
                            sourceAccountId,
                            destinationAccountId,
                            amount,
                            ex.getMessage()
                    ));
        }
    }

    @SagaEventHandler(associationProperty = "transferId")
    public void handle(MoneyWithdrawnEvent event) {

        try {

            commandGateway.sendAndWait(new DepositMoneyCommand(destinationAccountId, amount, transferId));

        } catch (Exception ex) {

            compensateTransfer(ex);
        }
    }

    private void compensateTransfer(Exception ex) {

        compensationInProgress = true;
        failureReason = ex.getMessage();

        try {
            commandGateway.sendAndWait(
                    new DepositMoneyCommand(
                            sourceAccountId,
                            amount,
                            transferId
                    ));
        }
        catch (Exception compensationException) {
            eventGateway.publish(
                    new CompensationFailedEvent(
                            transferId,
                            sourceAccountId,
                            destinationAccountId,
                            amount,
                            compensationException.getMessage()
                    ));
        }
    }

    @SagaEventHandler(associationProperty = "transferId")
    public void handle(MoneyDepositedEvent event) {

        if (compensationInProgress) {

            // This deposit is restoring money to the source account.
            eventGateway.publish(
                    new TransferFailedEvent(
                            transferId,
                            sourceAccountId,
                            destinationAccountId,
                            amount,
                            failureReason
                    )
            );

            return;
        }

        eventGateway.publish(
                new TransferCompletedEvent(
                        transferId,
                        sourceAccountId,
                        destinationAccountId,
                        amount
                )
        );
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transferId")
    public void handle(TransferCompletedEvent event) {
        // trannsfer completed

        idempotencyRepository
                .findByTransferId(event.getTransferId())
                .ifPresent(record -> { record.setStatus("COMPLETED");
                    idempotencyRepository.save(record);
                });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transferId")
    public void handle(TransferFailedEvent event) {

        // Transfer failed, compensation completed
        // or withdrawal failed.

        idempotencyRepository
                .findByTransferId(event.getTransferId())
                .ifPresent(record -> { record.setStatus("FAILED");
                    idempotencyRepository.save(record);
                });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transferId")
    public void handle(CompensationFailedEvent event) {

        // Automatic compensation failed.
        // The failure remains recorded for recovery.

        idempotencyRepository
                .findByTransferId(event.getTransferId())
                .ifPresent(record ->
                { record.setStatus("FAILED");
                    idempotencyRepository.save(record);
                });
    }

}

