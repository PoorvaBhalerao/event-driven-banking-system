package com.bank.event_driven_banking_system.command.controller;

import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.OpenAccountCommand;
import com.bank.event_driven_banking_system.command.commands.TransferMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.WithdrawMoneyCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/accounts")
public class AccountCommandController {

    private final CommandGateway commandGateway;

    public AccountCommandController(CommandGateway commandGateway)
    {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/open")
    public ResponseEntity<Map<String, String>> openAccount(@RequestBody OpenAccountRequest request)
    {
        String accountId = UUID.randomUUID().toString();

        OpenAccountCommand command = new OpenAccountCommand(accountId, request.getCustomerName(),
                request.getOpeningBalance());

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "accountId", accountId,
                "message", "Account created successfully. Account ID: " + accountId
        ));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> depositAccount(@RequestBody DepositRequest request)
    {
        DepositMoneyCommand command = new DepositMoneyCommand(request.getAccountId(), request.getAmount());

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Money Deposited Successfully"
        ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdrawAccount(@RequestBody WithdrawRequest request)
    {
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(request.getAccountId(), request.getAmount());

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Money Withdrawn Successfully"
        ));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transferAccount(@RequestBody TransferMoneyRequest request,
                                                                @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey)
    {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            idempotencyKey = UUID.randomUUID().toString();
        }
        String transferId = UUID.randomUUID().toString();
        TransferMoneyCommand command = new TransferMoneyCommand(
                transferId,
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getAmount(),
                idempotencyKey
        );

        String resultTransferId = commandGateway.sendAndWait(command);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "transferId", resultTransferId != null ? resultTransferId : transferId,
                "message", "Money Transfer Submitted Successfully. Transfer ID: " + (resultTransferId != null ? resultTransferId : transferId)
        ));
    }

    @PostMapping("/close")
    public ResponseEntity<Map<String, String>> closeAccount(@RequestBody CloseAccountRequest request)
    {
        com.bank.event_driven_banking_system.command.commands.CloseAccountCommand command =
                new com.bank.event_driven_banking_system.command.commands.CloseAccountCommand(
                        request.getAccountId(),
                        request.getReason()
                );

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Account Closed Successfully"
        ));
    }
}

