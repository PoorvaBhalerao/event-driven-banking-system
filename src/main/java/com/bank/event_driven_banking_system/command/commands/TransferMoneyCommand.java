package com.bank.event_driven_banking_system.command.commands;

public class TransferMoneyCommand {

    private String transferId;
    private String sourceAccountId;
    private String destinationAccountId;
    private double amount;

    private String idempotencyKey;

    public TransferMoneyCommand(String transferId, String sourceAccountId, String destinationAccountId, double amount) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
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

    public String getIdempotencyKey() { return idempotencyKey;}
}
