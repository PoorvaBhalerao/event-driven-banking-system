# Architecture Decision Record (ADR)

## ADR 001: Event-Driven Banking System Architecture with CQRS & Event Sourcing

### Status
**APPROVED**

---

## 1. Context & Problem Statement
Traditional CRUD-based banking systems face severe concurrency challenges, auditability gaps, and read/write scaling contention:
- Financial ledger accounts require 100% complete, immutable audit logs of every balance transaction.
- High-volume read queries (e.g. balance checks & transaction history) slow down critical command operations (e.g. deposits & fund transfers).
- Multi-step operations (e.g. inter-account transfers) risk inconsistent states if failure recovery or compensation is not designed properly.

---

## 2. Decision & Architectural Patterns Selected

### A. Command Query Responsibility Segregation (CQRS)
- **Command Side (Write):** Handles state mutations using Axon Framework Aggregates (`AccountAggregate`). Focuses strictly on business logic validation without database read constraints.
- **Query Side (Read):** Handles read projections stored in optimized JPA database tables (`AccountEntity`, `TransactionHistoryEntity`). Query projections subscribe to domain events to maintain eventual consistency.

### B. Event Sourcing & Axon EventStore
- State changes are stored as an immutable sequence of domain events (`AccountOpenedEvent`, `MoneyDepositedEvent`, `MoneyWithdrawnEvent`, `TransferInitiatedEvent`, `TransferCompletedEvent`, `TransferFailedEvent`, `CompensationFailedEvent`, `AccountClosedEvent`).
- Current aggregate state is rehydrated on-demand by replaying historical events.
- **Benefit:** Full zero-loss audit trails and complete event replay capabilities from position 0.

### C. Saga Pattern for Distributed Fund Transfers (`TransferSaga`)
- Coordinates multi-aggregate money transfers as a stateless/stateful transaction workflow.
- Automatically handles **compensating transactions** (reversing source account withdrawal if target account deposit fails).

### D. Apache Kafka Event Backbone
- Bridges Axon internal event stream with Apache Kafka topic `banking-domain-events`.
- Enables external consumer microservices (Notification Service, Analytics, Fraud Monitor) to stream banking events without coupling to internal domain aggregates.

### E. Testcontainers for Integration Testing
- Uses Testcontainers (`org.testcontainers:postgresql`, `org.testcontainers:kafka`) to run isolated containerized database and messaging components for automated integration tests.

---

## 3. Consequences & Trade-offs

| Positive Consequences | Neutral / Negative Trade-offs |
| :--- | :--- |
| **Auditability & Traceability:** Immutable domain events serve as the ultimate single source of truth. | **Eventual Consistency:** Brief millisecond delay between command publication and query table updates. |
| **Separation of Concerns:** Read projections can be optimized, indexed, or cached independently of write models. | **Schema Evolution Complexity:** Requires Event Upcasters (`AccountOpenedEventUpcaster`) when payload schemas change over time. |
| **Resilience & Replay:** Read databases can be wiped and fully rebuilt at any time via `POST /admin/replay-events`. | **System Complexity:** Higher initial setup effort compared to simple monolith CRUD applications. |
