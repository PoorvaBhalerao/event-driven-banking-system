package com.bank.event_driven_banking_system.query.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AccountEntity
{
    @Id
    private String accountId;

    private String customerName;

    private double balance;

    private String status = "ACTIVE";

    public AccountEntity() { }

    public AccountEntity(String accountId, String customerName, double balance)
    {
        this.accountId = accountId;
        this.customerName = customerName;
        this.balance = balance;
        this.status = "ACTIVE";
    }

    public AccountEntity(String accountId, String customerName, double balance, String status)
    {
        this.accountId = accountId;
        this.customerName = customerName;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
