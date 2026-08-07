package com.bank.event_driven_banking_system.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DepositMoneyCommand
{
    @TargetAggregateIdentifier              //Use accountId to locate the correct AccountAggregate
                                            // Axon automatically routes to particular account
    private String accountId;
    private double amount;

    public DepositMoneyCommand(String accountId, double amount) {
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
