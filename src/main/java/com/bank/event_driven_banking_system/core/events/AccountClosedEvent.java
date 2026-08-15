package com.bank.event_driven_banking_system.core.events;

public class AccountClosedEvent {

    private String accountId;
    private String reason;

    public AccountClosedEvent() {}

    public AccountClosedEvent(String accountId, String reason) {
        this.accountId = accountId;
        this.reason = reason;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getReason() {
        return reason;
    }
}
