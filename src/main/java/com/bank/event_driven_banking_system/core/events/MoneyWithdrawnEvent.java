package com.bank.event_driven_banking_system.core.events;

public class MoneyWithdrawnEvent {

    private String accountId;
    private double amount;
    private String transferId;

    public MoneyWithdrawnEvent() {}

    public MoneyWithdrawnEvent(String accountId, double amount) {
        this(accountId, amount, null);
    }

    public MoneyWithdrawnEvent(String accountId, double amount, String transferId) {
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
