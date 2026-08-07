package com.bank.event_driven_banking_system.command.controller;

public class OpenAccountRequest {
    // only contains two characteristics as server will automatically generates accountID
    private String customerName;
    private double openingBalance;

    public OpenAccountRequest() { }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

}
