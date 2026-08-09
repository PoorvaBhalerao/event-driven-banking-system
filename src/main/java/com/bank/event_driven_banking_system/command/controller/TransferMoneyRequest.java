package com.bank.event_driven_banking_system.command.controller;

public class TransferMoneyRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private double amount;

    public TransferMoneyRequest() { }

    public TransferMoneyRequest(String sourceAccountId, String destinationAccountId, double amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
