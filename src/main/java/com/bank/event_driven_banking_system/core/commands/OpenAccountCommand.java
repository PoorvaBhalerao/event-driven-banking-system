package com.bank.event_driven_banking_system.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class OpenAccountCommand {

    @TargetAggregateIdentifier
    private final String accountID;
    private final String customerName;
    private final double openingBalance;

    public OpenAccountCommand(String accountID, String customerName, double openingBalance) {
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
