package com.bank.event_driven_banking_system.core.events;

public class AccountOpenedEvent {

    private String accountId;
    private String customerName;
    private double openingBalance;
    private String currency = "USD";

    public AccountOpenedEvent() {}

    public AccountOpenedEvent(String accountId, String customerName, double openingBalance) {
        this.accountId = accountId;
        this.customerName = customerName;
        this.openingBalance = openingBalance;
        this.currency = "USD";
    }

    public AccountOpenedEvent(String accountId, String customerName, double openingBalance, String currency) {
        this.accountId = accountId;
        this.customerName = customerName;
        this.openingBalance = openingBalance;
        this.currency = currency;
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

    public String getCurrency() {
        return currency;
    }
}
