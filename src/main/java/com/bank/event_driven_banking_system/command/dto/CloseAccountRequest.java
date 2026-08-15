package com.bank.event_driven_banking_system.command.dto;

public class CloseAccountRequest {

    private String accountId;
    private String reason;

    public CloseAccountRequest() {
    }

    public CloseAccountRequest(String accountId, String reason) {
        this.accountId = accountId;
        this.reason = reason;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
