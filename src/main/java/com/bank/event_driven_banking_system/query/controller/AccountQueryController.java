package com.bank.event_driven_banking_system.query.controller;

import com.bank.event_driven_banking_system.query.dto.TransactionHistoryResponse;
import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import com.bank.event_driven_banking_system.query.service.TransactionHistoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/accounts")
public class AccountQueryController {

    private final AccountRepository accountRepository;
    private final TransactionHistoryService transactionHistoryService;

    public AccountQueryController(AccountRepository accountRepository,
                                  TransactionHistoryService transactionHistoryService) {
        this.accountRepository = accountRepository;
        this.transactionHistoryService = transactionHistoryService;
    }

    @GetMapping("/{accountId}")
    public AccountEntity getAccount(@PathVariable String accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found."));
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionHistoryResponse> getTransactionHistory(@PathVariable String accountId) {
        return transactionHistoryService.getTransactionHistory(accountId);
    }
}

