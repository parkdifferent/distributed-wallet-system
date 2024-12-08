package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEvent implements Event {
    /**
     * Event ID
     */
    protected String eventId;

    /**
     * Account ID that this event belongs to
     */
    protected String accountId;
    
    /**
     * Operator who triggered this event
     */
    protected String operatorId;

    /**
     * Timestamp when the event occurred
     */
    protected Instant timestamp;

    protected BaseEvent(String accountId, String operatorId) {
        this.eventId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.operatorId = operatorId;
        this.timestamp = Instant.now();
    }

    /**
     * Get event ID
     */
    @Override
    public String getEventId() {
        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        return eventId;
    }

    /**
     * Get timestamp in milliseconds since epoch
     */
    @Override
    public long getTimestampMillis() {
        return timestamp != null 
            ? timestamp.toEpochMilli() 
            : System.currentTimeMillis();
    }

    /**
     * Set timestamp from milliseconds since epoch
     */
    public void setTimestampMillis(long timestampMillis) {
        this.timestamp = Instant.ofEpochMilli(timestampMillis);
    }

    /**
     * Get event type name
     */
    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
