package com.bank.event_driven_banking_system.command.aggregate;

import com.bank.event_driven_banking_system.core.commands.CloseAccountCommand;
import com.bank.event_driven_banking_system.core.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.core.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.core.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.core.events.AccountClosedEvent;
import com.bank.event_driven_banking_system.core.events.AccountOpenedEvent;
import com.bank.event_driven_banking_system.core.events.MoneyDepositedEvent;
import com.bank.event_driven_banking_system.core.events.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;

    private String customerName;

    private double balance;

    private boolean active = true;

    // Required by Axon
    public AccountAggregate() {}

    @CommandHandler
    public AccountAggregate(OpenAccountCommand command) {
        if (command.getOpeningBalance() < 0) {
            throw new IllegalArgumentException("Account Opening Balance cannot be Negative");
        }

        AccountOpenedEvent event = new AccountOpenedEvent(
                command.getAccountID(),
                command.getCustomerName(),
                command.getOpeningBalance()
        );

        AggregateLifecycle.apply(event);
    }

    // Handles deposit into an existing account
    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        if (!active) {
            throw new IllegalStateException("Account is closed.");
        }

        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        MoneyDepositedEvent event = new MoneyDepositedEvent(
                command.getAccountId(),
                command.getAmount(),
                command.getTransferId()
        );

        AggregateLifecycle.apply(event);
    }

    // Handles withdrawal from an existing account
    @CommandHandler
    public void handle(WithdrawMoneyCommand command) {
        if (!active) {
            throw new IllegalStateException("Account is closed.");
        }

        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (this.balance < command.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(
                command.getAccountId(),
                command.getAmount(),
                command.getTransferId()
        );

        AggregateLifecycle.apply(event);
    }

    // Handles account closure
    @CommandHandler
    public void handle(CloseAccountCommand command) {
        if (!active) {
            throw new IllegalStateException("Account is already closed.");
        }

        if (this.balance > 0) {
            throw new IllegalStateException("Cannot close account with a non-zero balance.");
        }

        AccountClosedEvent event = new AccountClosedEvent(command.getAccountId(), command.getReason());

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent event) {
        this.accountId = event.getAccountId();
        this.customerName = event.getCustomerName();
        this.balance = event.getOpeningBalance();
        this.active = true;
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance += event.getAmount();
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance -= event.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.active = false;
    }
}
