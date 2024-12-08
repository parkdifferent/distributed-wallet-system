package com.wallet.command.event;

/**
 * Base interface for all events in the system
 */
public interface Event {
    /**
     * Get the event ID (used for idempotency)
     */
    String getEventId();

    /**
     * Get the account ID this event belongs to
     */
    String getAccountId();

    /**
     * Get timestamp in milliseconds since epoch
     */
    long getTimestampMillis();

    /**
     * Get event type name
     */
    String getEventType();

    /**
     * Get the ID of the operator who triggered this event
     */
    String getOperatorId();
}
