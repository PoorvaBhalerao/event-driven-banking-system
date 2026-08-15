package com.bank.event_driven_banking_system.command.idempotency;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "idempotency_records",
        uniqueConstraints ={
                @UniqueConstraint(
                        name = "uk_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "transfer_id", nullable = false)
    private String transferId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public IdempotencyRecord() { }

    public IdempotencyRecord(String idempotencyKey, String transferId, String status) {
        this.idempotencyKey = idempotencyKey;
        this.transferId = transferId;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
