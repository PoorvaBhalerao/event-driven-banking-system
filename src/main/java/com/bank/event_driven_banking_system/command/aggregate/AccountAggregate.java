package com.bank.event_driven_banking_system.command.aggregate;

import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.command.events.AccountOpenedEvent;
import com.bank.event_driven_banking_system.command.events.MoneyDepositedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate                                  //Marks this class as an Aggregate.
                                            // this class receives command and produces Event
public class AccountAggregate
{

    @AggregateIdentifier                    //This is the unique ID of this Aggregate.
    private String accountId;

    private String customerName;

    private double balance;

    //Required by Axon
    public AccountAggregate() {}            //Axon needs this constructor to recreate an
                                            // aggregate by replaying events from the event store.

    @CommandHandler                         // handles account creation command
    public AccountAggregate(OpenAccountCommand command)
    {
        if(command.getOpeningBalance() < 0)
        {
            throw new IllegalArgumentException("Account Opening Balance cannot be Negative");
        }

        AccountOpenedEvent event = new AccountOpenedEvent(command.getAccountID(), command.getCustomerName(), command.getOpeningBalance());

        AggregateLifecycle.apply(event);    // apply this event means dont save latest balance only save event
                                            // Axon stores Event, publishes it and calls @EventSourcingHandler
    }

    // Handles deposit into an existing account
    @CommandHandler
    public void handle(DepositMoneyCommand command)
    {

        if (command.getAmount() <= 0)
        {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        MoneyDepositedEvent event = new MoneyDepositedEvent(command.getAccountId(), command.getAmount());

        AggregateLifecycle.apply(event);
    }


    @EventSourcingHandler                   // Updates aggregate state after account creation
    public void on(AccountOpenedEvent event)
    {

        this.accountId = event.getAccountId();
        this.customerName = event.getCustomerName();
        this.balance = event.getOpeningBalance();
    }


    @EventSourcingHandler                   // Updates aggregate state after deposit
    public void on(MoneyDepositedEvent event)
    {

        this.balance += event.getAmount();
    }
}

