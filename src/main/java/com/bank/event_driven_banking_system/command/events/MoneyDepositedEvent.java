package com.bank.event_driven_banking_system.command.events;

public class MoneyDepositedEvent {

    private String accountId;
    private double amount;

    private String transferId;

    public MoneyDepositedEvent(String accountId, double amount)
    {
        this.accountId = accountId;
        this.amount = amount;
    }

    // New constructor - for transfer
    public MoneyDepositedEvent(
            String accountId,
            double amount,
            String transferId) {

        this.accountId = accountId;
        this.amount = amount;
        this.transferId = transferId;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransferId() {
        return transferId;
    }
}
