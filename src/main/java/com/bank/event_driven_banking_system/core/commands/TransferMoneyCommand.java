package com.bank.event_driven_banking_system.core.commands;

public class TransferMoneyCommand {

    private final String transferId;
    private final String sourceAccountId;
    private final String destinationAccountId;
    private final double amount;
    private final String idempotencyKey;

    public TransferMoneyCommand(String transferId, String sourceAccountId, String destinationAccountId, double amount) {
        this(transferId, sourceAccountId, destinationAccountId, amount, null);
    }

    public TransferMoneyCommand(String transferId, String sourceAccountId, String destinationAccountId,
                                double amount, String idempotencyKey) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
