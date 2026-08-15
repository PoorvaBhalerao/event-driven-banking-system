package com.bank.event_driven_banking_system.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DepositMoneyCommand {

    @TargetAggregateIdentifier
    private final String accountId;
    private final double amount;
    private final String transferId;

    public DepositMoneyCommand(String accountId, double amount) {
        this(accountId, amount, null);
    }

    public DepositMoneyCommand(String accountId, double amount, String transferId) {
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
