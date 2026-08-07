package com.bank.event_driven_banking_system.command.events;

public class AccountOpenedEvent {

    private String accountId;
    private String customerName;
    private double openingBalance;

    public AccountOpenedEvent(String accountId,
                              String customerName,
                              double openingBalance) {

        this.accountId = accountId;
        this.customerName = customerName;
        this.openingBalance = openingBalance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }
}
