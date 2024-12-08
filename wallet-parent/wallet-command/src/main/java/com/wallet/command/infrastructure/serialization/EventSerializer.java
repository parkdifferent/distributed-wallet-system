package com.wallet.command.infrastructure.serialization;

import com.wallet.command.event.BaseEvent;
import java.util.List;

/**
 * Event serialization interface for wallet system events
 */
public interface EventSerializer {
    /**
     * Serialize an event to JSON string
     *
     * @param event The event to serialize
     * @return JSON string representation of the event
     */
    String serialize(BaseEvent event);

    /**
     * Serialize a list of events to JSON string
     *
     * @param events The events to serialize
     * @return JSON string representation of the events
     */
    String serializeList(List<BaseEvent> events);

    /**
     * Deserialize JSON string to an event of specified type
     *
     * @param data The JSON string to deserialize
     * @param eventType The fully qualified class name of the event type
     * @return Deserialized event
     * @throws EventDeserializationException if deserialization fails
     */
    BaseEvent deserialize(String data, String eventType) throws EventDeserializationException;

    /**
     * Deserialize JSON string to a list of events
     *
     * @param data The JSON string to deserialize
     * @param eventTypes List of fully qualified class names for each event
     * @return List of deserialized events
     * @throws EventDeserializationException if deserialization fails
     */
    List<BaseEvent> deserializeList(String data, List<String> eventTypes) throws EventDeserializationException;
}
