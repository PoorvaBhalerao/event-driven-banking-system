package com.bank.event_driven_banking_system.command.dto;

public class OpenAccountRequest {

    private String customerName;
    private double openingBalance;

    public OpenAccountRequest() {
    }

    public OpenAccountRequest(String customerName, double openingBalance) {
        this.customerName = customerName;
        this.openingBalance = openingBalance;
    }

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
