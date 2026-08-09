package com.bank.event_driven_banking_system.command.aggregate;

import com.bank.event_driven_banking_system.command.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.command.events.AccountOpenedEvent;
import com.bank.event_driven_banking_system.command.events.MoneyWithdrawnEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountAggregateTest {

    private FixtureConfiguration<AccountAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    @Test
    public void testWithdrawMoney_Success() {
        fixture.given(new AccountOpenedEvent("A123", "Rahul", 1000.0))
                .when(new WithdrawMoneyCommand("A123", 300.0))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new MoneyWithdrawnEvent("A123", 300.0));
    }

    @Test
    public void testWithdrawMoney_NegativeAmount() {
        fixture.given(new AccountOpenedEvent("A123", "Rahul", 1000.0))
                .when(new WithdrawMoneyCommand("A123", -100.0))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testWithdrawMoney_ZeroAmount() {
        fixture.given(new AccountOpenedEvent("A123", "Rahul", 1000.0))
                .when(new WithdrawMoneyCommand("A123", 0.0))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testWithdrawMoney_InsufficientBalance() {
        fixture.given(new AccountOpenedEvent("A123", "Rahul", 500.0))
                .when(new WithdrawMoneyCommand("A123", 600.0))
                .expectException(IllegalArgumentException.class);
    }
}
