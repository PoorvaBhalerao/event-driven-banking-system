package com.bank.event_driven_banking_system.command.idempotency.repository;

import com.bank.event_driven_banking_system.command.idempotency.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long>
{
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    Optional<IdempotencyRecord> findByTransferId(String transferId);        //here transferId because Our terminal events contain: transferId
                                                                            // but they don't contain the original idempotencyKey.
}
