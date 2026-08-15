package com.bank.event_driven_banking_system.core.events;

public class TransferFailedEvent {

    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private double amount;
    private String reason;

    public TransferFailedEvent() {}

    public TransferFailedEvent(String transferId, String sourceAccountId, String destinationAccountId, double amount, String reason) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }
}
