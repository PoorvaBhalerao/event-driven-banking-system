package com.bank.event_driven_banking_system.query.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryResponse {

    private String eventId;
    private String accountId;
    private String eventType;
    private Double amount;
    private Instant timestamp;
    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private String reason;

    public TransactionHistoryResponse() {
    }

    public TransactionHistoryResponse(String eventId,
                                      String accountId,
                                      String eventType,
                                      Double amount,
                                      Instant timestamp,
                                      String transferId,
                                      String sourceAccountId,
                                      String destinationAccountId,
                                      String reason) {
        this.eventId = eventId;
        this.accountId = accountId;
        this.eventType = eventType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.reason = reason;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(String destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
