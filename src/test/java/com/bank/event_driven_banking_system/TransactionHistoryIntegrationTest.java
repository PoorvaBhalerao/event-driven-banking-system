package com.bank.event_driven_banking_system;

import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.command.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.query.dto.TransactionHistoryResponse;
import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import com.bank.event_driven_banking_system.query.service.TransactionHistoryService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TransactionHistoryIntegrationTest.AxonTestConfig.class)
public class TransactionHistoryIntegrationTest {

    @TestConfiguration
    static class AxonTestConfig {
        @Autowired
        public void configure(EventProcessingConfigurer configurer) {
            configurer.usingSubscribingEventProcessors();
        }
    }

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    public void testAccountHistoryAfterAccountCreation() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "John Doe", 1000.0));

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);

        assertEquals(1, history.size());
        TransactionHistoryResponse tx = history.get(0);
        assertEquals(accountId, tx.getAccountId());
        assertEquals("ACCOUNT_OPENED", tx.getEventType());
        assertEquals(1000.0, tx.getAmount());
        assertNotNull(tx.getEventId());
        assertNotNull(tx.getTimestamp());

        // Verify endpoint response
        mockMvc.perform(get("/accounts/" + accountId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("ACCOUNT_OPENED"))
                .andExpect(jsonPath("$[0].amount").value(1000.0));
    }

    @Test
    public void testAccountHistoryAfterDeposit() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Jane Doe", 500.0));
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, 200.0));

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);

        assertEquals(2, history.size());
        assertEquals("ACCOUNT_OPENED", history.get(0).getEventType());
        assertEquals("MONEY_DEPOSITED", history.get(1).getEventType());
        assertEquals(200.0, history.get(1).getAmount());

        mockMvc.perform(get("/accounts/" + accountId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].eventType").value("MONEY_DEPOSITED"))
                .andExpect(jsonPath("$[1].amount").value(200.0));
    }

    @Test
    public void testAccountHistoryAfterWithdrawal() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Alice", 1000.0));
        commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, 300.0));

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);

        assertEquals(2, history.size());
        assertEquals("ACCOUNT_OPENED", history.get(0).getEventType());
        assertEquals("MONEY_WITHDRAWN", history.get(1).getEventType());
        assertEquals(300.0, history.get(1).getAmount());

        mockMvc.perform(get("/accounts/" + accountId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].eventType").value("MONEY_WITHDRAWN"))
                .andExpect(jsonPath("$[1].amount").value(300.0));
    }

    @Test
    public void testAccountHistoryAfterTransfer() throws Exception {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Alice", 1000.0));
        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Bob", 500.0));

        String transferId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, destId, 400.0));

        List<TransactionHistoryResponse> sourceHistory = transactionHistoryService.getTransactionHistory(sourceId);
        List<TransactionHistoryResponse> destHistory = transactionHistoryService.getTransactionHistory(destId);

        // Source account has AccountOpenedEvent and MoneyWithdrawnEvent (from debit)
        assertEquals(2, sourceHistory.size());
        assertEquals("ACCOUNT_OPENED", sourceHistory.get(0).getEventType());
        assertEquals("MONEY_WITHDRAWN", sourceHistory.get(1).getEventType());
        assertEquals(400.0, sourceHistory.get(1).getAmount());

        // Destination account has AccountOpenedEvent and MoneyDepositedEvent (from credit)
        assertEquals(2, destHistory.size());
        assertEquals("ACCOUNT_OPENED", destHistory.get(0).getEventType());
        assertEquals("MONEY_DEPOSITED", destHistory.get(1).getEventType());
        assertEquals(400.0, destHistory.get(1).getAmount());
    }

    @Test
    public void testMultipleEventsChronologicalOrder() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Charlie", 1000.0));
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, 500.0));
        commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, 200.0));
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, 100.0));

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);

        assertEquals(4, history.size());
        assertEquals("ACCOUNT_OPENED", history.get(0).getEventType());
        assertEquals(1000.0, history.get(0).getAmount());

        assertEquals("MONEY_DEPOSITED", history.get(1).getEventType());
        assertEquals(500.0, history.get(1).getAmount());

        assertEquals("MONEY_WITHDRAWN", history.get(2).getEventType());
        assertEquals(200.0, history.get(2).getAmount());

        assertEquals("MONEY_DEPOSITED", history.get(3).getEventType());
        assertEquals(100.0, history.get(3).getAmount());

        // Ensure timestamps are chronologically ordered
        assertTrue(history.get(0).getTimestamp().isBefore(history.get(1).getTimestamp()) ||
                history.get(0).getTimestamp().equals(history.get(1).getTimestamp()));
    }

    @Test
    public void testUnknownAccountReturns404() throws Exception {
        String unknownId = UUID.randomUUID().toString();

        assertThrows(RuntimeException.class, () ->
                transactionHistoryService.getTransactionHistory(unknownId)
        );

        mockMvc.perform(get("/accounts/" + unknownId + "/transactions"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEmptyHistoryForExistingAccount() throws Exception {
        // If an account is present in repo but has no events
        String accountId = UUID.randomUUID().toString();
        AccountEntity entity = new AccountEntity(accountId, "Ghost User", 0.0);
        accountRepository.save(entity);

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);
        assertNotNull(history);
        assertTrue(history.isEmpty());

        mockMvc.perform(get("/accounts/" + accountId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testTransactionDTOMapping() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Dave", 750.0));

        List<TransactionHistoryResponse> history = transactionHistoryService.getTransactionHistory(accountId);
        assertEquals(1, history.size());
        TransactionHistoryResponse dto = history.get(0);

        assertNotNull(dto.getEventId());
        assertEquals(accountId, dto.getAccountId());
        assertEquals("ACCOUNT_OPENED", dto.getEventType());
        assertEquals(750.0, dto.getAmount());
        assertNotNull(dto.getTimestamp());
        assertNull(dto.getTransferId());
        assertNull(dto.getSourceAccountId());
        assertNull(dto.getDestinationAccountId());
        assertNull(dto.getReason());
    }

    @Test
    public void testExistingAccountQueryStillWorks() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Eve", 1200.0));

        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerName").value("Eve"))
                .andExpect(jsonPath("$.balance").value(1200.0));
    }
}
