package com.bank.event_driven_banking_system.query.controller;

import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountQueryController {

    private final AccountRepository accountRepository;

    public AccountQueryController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}")
    public AccountEntity getAccount(@PathVariable String accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found."));
    }
}
