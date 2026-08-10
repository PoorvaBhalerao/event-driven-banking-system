package com.bank.event_driven_banking_system.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DepositMoneyCommand
{
    @TargetAggregateIdentifier              //Use accountId to locate the correct AccountAggregate
                                            // Axon automatically routes to particular account
    private String accountId;
    private double amount;

    private String transferId;

    public DepositMoneyCommand(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    // New constructor - for transfer
    public DepositMoneyCommand(
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
