package com.wallet.command.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wallet.command.event.BaseEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventSerializerImpl implements EventSerializer {
    private final ObjectMapper objectMapper;

    public EventSerializerImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String serialize(BaseEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to serialize event: " + event, e);
        }
    }

    @Override
    public String serializeList(List<BaseEvent> events) {
        try {
            return objectMapper.writeValueAsString(events);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to serialize event list", e);
        }
    }

    @Override
    public BaseEvent deserialize(String data, String eventType) throws EventDeserializationException {
        try {
            Class<? extends BaseEvent> eventClass = getEventClass(eventType);
            return objectMapper.readValue(data, eventClass);
        } catch (Exception e) {
            throw new EventDeserializationException(
                String.format("Failed to deserialize event of type %s: %s", eventType, data), e);
        }
    }

    @Override
    public List<BaseEvent> deserializeList(String data, List<String> eventTypes) throws EventDeserializationException {
        try {
            List<BaseEvent> events = new ArrayList<>();
            List<Object> rawEvents = objectMapper.readValue(data, List.class);
            
            if (rawEvents.size() != eventTypes.size()) {
                throw new EventDeserializationException(
                    "Number of events does not match number of event types");
            }

            for (int i = 0; i < rawEvents.size(); i++) {
                String eventData = objectMapper.writeValueAsString(rawEvents.get(i));
                events.add(deserialize(eventData, eventTypes.get(i)));
            }

            return events;
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event list", e);
        }
    }

    private Class<? extends BaseEvent> getEventClass(String eventType) throws ClassNotFoundException {
        String className = "com.wallet.command.event." + eventType;
        @SuppressWarnings("unchecked")
        Class<? extends BaseEvent> eventClass = (Class<? extends BaseEvent>) Class.forName(className);
        return eventClass;
    }
}
