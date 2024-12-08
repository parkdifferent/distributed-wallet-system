package com.wallet.command.infrastructure.serialization;

/**
 * Exception thrown when event deserialization fails
 */
public class EventDeserializationException extends RuntimeException {
    public EventDeserializationException(String message) {
        super(message);
    }

    public EventDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventDeserializationException(String eventType, String data, Throwable cause) {
        super(String.format("Failed to deserialize event of type %s. Data: %s", eventType, data), cause);
    }
}
