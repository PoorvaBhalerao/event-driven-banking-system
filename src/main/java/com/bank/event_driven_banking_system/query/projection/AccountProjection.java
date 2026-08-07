package com.bank.event_driven_banking_system.query.projection;

import com.bank.event_driven_banking_system.command.events.AccountOpenedEvent;
import com.bank.event_driven_banking_system.command.events.MoneyDepositedEvent;
import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class AccountProjection
{
    private final AccountRepository accountRepository;

    public AccountProjection(AccountRepository accountRepository)
    {
        this.accountRepository = accountRepository;
    }

    @EventHandler
    public void on(AccountOpenedEvent event)
    {
        AccountEntity account = new AccountEntity(event.getAccountId(), event.getCustomerName(),
                event.getOpeningBalance());

        accountRepository.save(account);

        System.out.println("Account saved in query database");

    }

    @EventHandler
    public void on(MoneyDepositedEvent event)
    {
        AccountEntity account =
                accountRepository.findById(event.getAccountId())
                        .orElseThrow(() ->
                                new RuntimeException("Account not found"));

        account.setBalance(
                account.getBalance() + event.getAmount());

        accountRepository.save(account);

        System.out.println("Deposit updated in Query Database.");
    }
}
