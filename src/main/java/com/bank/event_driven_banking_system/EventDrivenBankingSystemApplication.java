package com.bank.event_driven_banking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.bank.event_driven_banking_system",
		"org.axonframework.eventsourcing.eventstore.jpa",
		"org.axonframework.eventhandling.tokenstore.jpa"
})
public class EventDrivenBankingSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(EventDrivenBankingSystemApplication.class, args);
	}

}

