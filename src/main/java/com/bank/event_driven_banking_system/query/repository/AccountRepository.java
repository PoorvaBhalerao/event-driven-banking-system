package com.bank.event_driven_banking_system.query.repository;

import com.bank.event_driven_banking_system.query.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// we dont write class here because Spring Boot creates the implementation automatically.
//we don't have to write:save(), findById(), findAll(), delete()
public interface AccountRepository extends JpaRepository<AccountEntity, String> // here String is datatype of primary key i e accountId
{

}
