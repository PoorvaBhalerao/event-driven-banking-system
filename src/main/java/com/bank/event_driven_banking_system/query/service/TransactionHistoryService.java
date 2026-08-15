package com.bank.event_driven_banking_system.query.service;

import com.bank.event_driven_banking_system.core.events.*;
import com.bank.event_driven_banking_system.query.dto.TransactionHistoryResponse;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventhandling.DomainEventMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionHistoryService {

    private final EventStore eventStore;
    private final AccountRepository accountRepository;

    public TransactionHistoryService(EventStore eventStore, AccountRepository accountRepository) {
        this.eventStore = eventStore;
        this.accountRepository = accountRepository;
    }

    public List<TransactionHistoryResponse> getTransactionHistory(String accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found.");
        }

        List<TransactionHistoryResponse> history = new ArrayList<>();
        DomainEventStream eventStream = eventStore.readEvents(accountId);

        while (eventStream.hasNext()) {
            DomainEventMessage<?> domainEvent = eventStream.next();
            Object payload = domainEvent.getPayload();

            TransactionHistoryResponse response = mapToResponse(domainEvent, payload, accountId);
            if (response != null) {
                history.add(response);
            }
        }

        return history;
    }

    private TransactionHistoryResponse mapToResponse(DomainEventMessage<?> domainEvent, Object payload, String queriedAccountId) {
        TransactionHistoryResponse response = new TransactionHistoryResponse();
        response.setEventId(domainEvent.getIdentifier());
        response.setTimestamp(domainEvent.getTimestamp());

        if (payload instanceof AccountOpenedEvent event) {
            response.setAccountId(event.getAccountId());
            response.setEventType("ACCOUNT_OPENED");
            response.setAmount(event.getOpeningBalance());
        } else if (payload instanceof MoneyDepositedEvent event) {
            response.setAccountId(event.getAccountId());
            response.setEventType("MONEY_DEPOSITED");
            response.setAmount(event.getAmount());
        } else if (payload instanceof MoneyWithdrawnEvent event) {
            response.setAccountId(event.getAccountId());
            response.setEventType("MONEY_WITHDRAWN");
            response.setAmount(event.getAmount());
        } else if (payload instanceof TransferInitiatedEvent event) {
            response.setAccountId(queriedAccountId);
            response.setEventType("TRANSFER_INITIATED");
            response.setAmount(event.getAmount());
            response.setTransferId(event.getTransferId());
            response.setSourceAccountId(event.getSourceAccountId());
            response.setDestinationAccountId(event.getDestinationAccountId());
        } else if (payload instanceof TransferCompletedEvent event) {
            response.setAccountId(queriedAccountId);
            response.setEventType("TRANSFER_COMPLETED");
            response.setAmount(event.getAmount());
            response.setTransferId(event.getTransferId());
            response.setSourceAccountId(event.getSourceAccountId());
            response.setDestinationAccountId(event.getDestinationAccountId());
        } else if (payload instanceof TransferFailedEvent event) {
            response.setAccountId(queriedAccountId);
            response.setEventType("TRANSFER_FAILED");
            response.setAmount(event.getAmount());
            response.setTransferId(event.getTransferId());
            response.setSourceAccountId(event.getSourceAccountId());
            response.setDestinationAccountId(event.getDestinationAccountId());
            response.setReason(event.getReason());
        } else {
            response.setAccountId(queriedAccountId);
            response.setEventType(payload.getClass().getSimpleName());
        }

        return response;
    }
}
