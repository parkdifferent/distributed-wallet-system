package com.wallet.command.model;

import com.wallet.command.event.BaseEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

/**
 * Base class for all commands in the domain.
 * Commands represent user intentions that will be processed to generate events.
 * Each command targets a specific account and may generate one or more events.
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Command {
    /**
     * Command ID for idempotency and tracing
     */
    private String commandId;

    /**
     * Account ID that this command targets
     */
    private String accountId;

    /**
     * Operator ID that this command is executed by
     */
    private String operatorId;

    /**
     * Reason for this command
     */
    private String reason;

    /**
     * Expected version for optimistic concurrency control
     */
    private Long version;

    /**
     * Command timestamp for auditing and event ordering
     */
    private Instant timestamp;

    protected Command(String accountId, String operatorId, String reason) {
        this.commandId = java.util.UUID.randomUUID().toString();
        this.accountId = accountId;
        this.operatorId = operatorId;
        this.reason = reason;
        this.timestamp = Instant.now();
    }

    /**
     * Get aggregate ID (alias for accountId for backward compatibility)
     * @deprecated Use getAccountId() instead
     */
    @Deprecated
    public String getAggregateId() {
        return accountId;
    }

    /**
     * Set aggregate ID (alias for accountId for backward compatibility)
     * @deprecated Use setAccountId() instead
     */
    @Deprecated
    public void setAggregateId(String aggregateId) {
        this.accountId = aggregateId;
    }

    /**
     * Validate the command before execution
     * @throws IllegalArgumentException if validation fails
     */
    public abstract void validate();

    /**
     * Execute the command on the given aggregate root
     * @param aggregate The aggregate root to execute the command on
     * @return List of events generated by this command
     * @throws IllegalStateException if the command cannot be executed in the current state
     */
    public abstract List<BaseEvent> execute(AggregateRoot aggregate);

    /**
     * Get command type name
     */
    public String getCommandType() {
        return this.getClass().getSimpleName();
    }
}
