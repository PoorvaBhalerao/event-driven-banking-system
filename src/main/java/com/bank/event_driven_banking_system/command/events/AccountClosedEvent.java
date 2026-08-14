package com.bank.event_driven_banking_system.command.events;

import java.time.Instant;

public class AccountClosedEvent {

    private final String accountId;
    private final String reason;
    private final Instant timestamp;

    public AccountClosedEvent(String accountId, String reason) {
        this.accountId = accountId;
        this.reason = reason;
        this.timestamp = Instant.now();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountClosedEvent that = (AccountClosedEvent) o;
        return java.util.Objects.equals(accountId, that.accountId) &&
                java.util.Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(accountId, reason);
    }
}
