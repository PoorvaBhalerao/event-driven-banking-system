package com.bank.event_driven_banking_system.command.events;

public class MoneyDepositedEvent {

    private String accountId;
    private double amount;

    public MoneyDepositedEvent(String accountId, double amount)
    {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }
}
