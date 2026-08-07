package com.bank.event_driven_banking_system.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class OpenAccountCommand {

    @TargetAggregateIdentifier
    private String accountID;

    private String customerName;

    private double openingBalance;

    public OpenAccountCommand(String accountID, String customerName, double openingBalance)
    {
        this.accountID = accountID;
        this.customerName = customerName;
        this.openingBalance = openingBalance;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }
}
