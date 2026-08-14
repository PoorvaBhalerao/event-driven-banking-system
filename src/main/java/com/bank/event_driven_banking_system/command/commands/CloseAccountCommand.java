package com.bank.event_driven_banking_system.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CloseAccountCommand {

    @TargetAggregateIdentifier
    private final String accountId;
    private final String reason;

    public CloseAccountCommand(String accountId, String reason) {
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
