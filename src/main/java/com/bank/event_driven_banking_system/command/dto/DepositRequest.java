package com.bank.event_driven_banking_system.command.dto;

public class DepositRequest {

    private String accountId;
    private double amount;

    public DepositRequest() {
    }

    public DepositRequest(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
