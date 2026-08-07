package com.bank.event_driven_banking_system.command.controller;

import com.bank.event_driven_banking_system.command.commands.DepositMoneyCommand;
import com.bank.event_driven_banking_system.command.commands.OpenAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountCommandController {

    private final CommandGateway commandGateway;

    public AccountCommandController(CommandGateway commandGateway)
    {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/open")
    public String openAccount(@RequestBody OpenAccountRequest request)
    {
        String accountId = UUID.randomUUID().toString();        // create ramdon accountID Every account gets unique Id

        OpenAccountCommand command = new OpenAccountCommand(accountId, request.getCustomerName(),
                request.getOpeningBalance());                   // this controller is not opening account its simple preparing command

        commandGateway.sendAndWait(command);                           // controller says, Axon pls handle this command
        //From there, Axon finds the correct AccountAggregate, executes the @CommandHandler, creates an AccountOpenedEvent, and updates the aggregate through the @EventSourcingHandler.
        //sendAndWait() waits until the command has been completely processed.

        return "Account creation request submitted successfully. Account ID: " + accountId;
    }

    @PostMapping("/deposit")
    public String depositAccount(@RequestBody DepositRequest request)
    {
        DepositMoneyCommand command = new DepositMoneyCommand(request.getAccountId(), request.getAmount());

        commandGateway.sendAndWait(command);                           // controller says, Axon pls handle this command
        //From there, Axon finds the correct AccountAggregate, executes the @CommandHandler, creates an AccountOpenedEvent, and updates the aggregate through the @EventSourcingHandler.
        //sendAndWait() waits until the command has been completely processed.

        return "Money Deposited Successfully";
    }

}
