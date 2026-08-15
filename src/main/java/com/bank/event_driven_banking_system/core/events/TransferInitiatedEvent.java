package com.bank.event_driven_banking_system.core.events;

public class TransferInitiatedEvent {

    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private double amount;

    public TransferInitiatedEvent() {}

    public TransferInitiatedEvent(String transferId, String sourceAccountId, String destinationAccountId, double amount) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public double getAmount() {
        return amount;
    }
}
