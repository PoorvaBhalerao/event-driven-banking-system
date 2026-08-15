package com.bank.event_driven_banking_system;

import com.bank.event_driven_banking_system.core.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.core.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.core.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.core.commands.WithdrawMoneyCommand;
import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import com.bank.event_driven_banking_system.query.repository.AccountRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(AccountWithdrawAndTransferTest.AxonTestConfig.class)
public class AccountWithdrawAndTransferTest {

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

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
    }

    // --- WITHDRAW TESTS ---

    @Test
    public void testWithdraw_SuccessAndProjectionUpdate() throws Exception {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Rahul", 1000.0));

        commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, 300.0));

        AccountEntity account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(700.0, account.getBalance());
    }

    @Test
    public void testWithdraw_NegativeAmount_Rejected() {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Rahul", 1000.0));

        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, -50.0))
        );

        AccountEntity account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    public void testWithdraw_ZeroAmount_Rejected() {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Rahul", 1000.0));

        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, 0.0))
        );

        AccountEntity account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    public void testWithdraw_InsufficientBalance_Rejected() {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Rahul", 500.0));

        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, 600.0))
        );

        AccountEntity account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(500.0, account.getBalance());
    }

    @Test
    public void testWithdraw_AccountNotFound() {
        String nonExistentId = UUID.randomUUID().toString();

        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new WithdrawMoneyCommand(nonExistentId, 100.0))
        );
    }

    // --- TRANSFER TESTS ---

    @Test
    public void testTransfer_SuccessAndReadModelsUpdate() {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Rahul", 1000.0));
        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Amit", 500.0));

        String transferId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, destId, 300.0, UUID.randomUUID().toString()));

        AccountEntity sourceAcc = accountRepository.findById(sourceId).orElseThrow();
        AccountEntity destAcc = accountRepository.findById(destId).orElseThrow();

        assertEquals(700.0, sourceAcc.getBalance());
        assertEquals(800.0, destAcc.getBalance());
    }

    @Test
    public void testTransfer_ZeroAmount_Rejected() {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Rahul", 1000.0));
        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Amit", 500.0));

        String transferId = UUID.randomUUID().toString();
        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, destId, 0.0, UUID.randomUUID().toString()))
        );

        AccountEntity sourceAcc = accountRepository.findById(sourceId).orElseThrow();
        AccountEntity destAcc = accountRepository.findById(destId).orElseThrow();

        assertEquals(1000.0, sourceAcc.getBalance());
        assertEquals(500.0, destAcc.getBalance());
    }

    @Test
    public void testTransfer_NegativeAmount_Rejected() {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Rahul", 1000.0));
        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Amit", 500.0));

        String transferId = UUID.randomUUID().toString();
        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, destId, -200.0, UUID.randomUUID().toString()))
        );

        AccountEntity sourceAcc = accountRepository.findById(sourceId).orElseThrow();
        AccountEntity destAcc = accountRepository.findById(destId).orElseThrow();

        assertEquals(1000.0, sourceAcc.getBalance());
        assertEquals(500.0, destAcc.getBalance());
    }

    @Test
    public void testTransfer_InsufficientSourceBalance_Rejected() {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Rahul", 200.0));
        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Amit", 500.0));

        String transferId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, destId, 500.0, UUID.randomUUID().toString()));

        AccountEntity sourceAcc = accountRepository.findById(sourceId).orElseThrow();
        AccountEntity destAcc = accountRepository.findById(destId).orElseThrow();

        assertEquals(200.0, sourceAcc.getBalance());
        assertEquals(500.0, destAcc.getBalance());
    }

    @Test
    public void testTransfer_SourceAccountNotFound() {
        String nonExistentSource = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(destId, "Amit", 500.0));

        String transferId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new TransferMoneyCommand(transferId, nonExistentSource, destId, 100.0, UUID.randomUUID().toString()));

        AccountEntity destAcc = accountRepository.findById(destId).orElseThrow();
        assertEquals(500.0, destAcc.getBalance());
    }

    @Test
    public void testTransfer_DestinationAccountNotFound() {
        String sourceId = UUID.randomUUID().toString();
        String nonExistentDest = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(sourceId, "Rahul", 1000.0));

        String transferId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new TransferMoneyCommand(transferId, sourceId, nonExistentDest, 100.0, UUID.randomUUID().toString()));

        AccountEntity sourceAcc = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(1000.0, sourceAcc.getBalance());
    }

    @Test
    public void testTransfer_SourceAndDestinationSame_Rejected() {
        String accountId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new OpenAccountCommand(accountId, "Rahul", 1000.0));

        String transferId = UUID.randomUUID().toString();
        assertThrows(Exception.class, () ->
                commandGateway.sendAndWait(new TransferMoneyCommand(transferId, accountId, accountId, 100.0, UUID.randomUUID().toString()))
        );
    }
}
