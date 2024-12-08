package com.wallet.command.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wallet.command.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JacksonEventSerializer implements EventSerializer {
    private final ObjectMapper objectMapper;

    public JacksonEventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String serialize(BaseEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            log.error("Failed to serialize event of type {}", event.getClass().getName(), e);
            throw new EventDeserializationException("Failed to serialize event", e);
        }
    }

    @Override
    public String serializeList(List<BaseEvent> events) {
        try {
            return objectMapper.writeValueAsString(events);
        } catch (Exception e) {
            log.error("Failed to serialize event list", e);
            throw new EventDeserializationException("Failed to serialize event list", e);
        }
    }

    @Override
    public BaseEvent deserialize(String data, String eventType) throws EventDeserializationException {
        try {
            Class<?> eventClass = Class.forName(eventType);
            return (BaseEvent) objectMapper.readValue(data, eventClass);
        } catch (ClassNotFoundException e) {
            log.error("Event type not found: {}", eventType, e);
            throw new EventDeserializationException("Event type not found: " + eventType, e);
        } catch (Exception e) {
            log.error("Failed to deserialize event of type {}", eventType, e);
            throw new EventDeserializationException(eventType, data, e);
        }
    }

    @Override
    public List<BaseEvent> deserializeList(String data, List<String> eventTypes) throws EventDeserializationException {
        try {
            List<BaseEvent> events = new ArrayList<>();
            List<?> rawEvents = objectMapper.readValue(data, List.class);

            if (rawEvents.size() != eventTypes.size()) {
                throw new EventDeserializationException(
                    String.format("Event count mismatch. Expected %d events but found %d",
                        eventTypes.size(), rawEvents.size()));
            }

            for (int i = 0; i < rawEvents.size(); i++) {
                String eventData = objectMapper.writeValueAsString(rawEvents.get(i));
                events.add(deserialize(eventData, eventTypes.get(i)));
            }

            return events;
        } catch (EventDeserializationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to deserialize event list", e);
            throw new EventDeserializationException("Failed to deserialize event list", e);
        }
    }
}
