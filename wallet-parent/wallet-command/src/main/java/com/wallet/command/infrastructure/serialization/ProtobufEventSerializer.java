package com.wallet.command.infrastructure.serialization;

import com.wallet.command.event.*;
import com.wallet.command.event.proto.*;
import com.wallet.enums.AssetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProtobufEventSerializer implements EventSerializer {

    @Override
    public String serialize(BaseEvent event) {
        // Implementation for serializing a single event
        return serializeEvent(event);
    }

    @Override
    public String serializeList(List<BaseEvent> events) {
        // Implementation for serializing a list of events
        return events.stream()
                .map(this::serializeEvent)
                .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public BaseEvent deserialize(String data, String eventType) throws EventDeserializationException {
        try {
            return deserializeEvent(data, eventType);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event", e);
        }
    }

    @Override
    public List<BaseEvent> deserializeList(String data, List<String> eventTypes) throws EventDeserializationException {
        try {
            List<BaseEvent> events = new ArrayList<>();
            String[] eventDataArray = data.substring(1, data.length() - 1).split(",");
            
            if (eventDataArray.length != eventTypes.size()) {
                throw new EventDeserializationException("Number of events does not match number of event types");
            }
            
            for (int i = 0; i < eventDataArray.length; i++) {
                events.add(deserializeEvent(eventDataArray[i], eventTypes.get(i)));
            }
            
            return events;
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event list", e);
        }
    }

    private String serializeEvent(BaseEvent event) {
        // Implementation for specific event types
        if (event instanceof AccountCreatedEvent) {
            return serializeAccountCreatedEvent((AccountCreatedEvent) event);
        } else if (event instanceof BalanceChangedEvent) {
            return serializeBalanceChangedEvent((BalanceChangedEvent) event);
        } else if (event instanceof AccountFrozenEvent) {
            return serializeAccountFrozenEvent((AccountFrozenEvent) event);
        } else if (event instanceof AccountUnfrozenEvent) {
            return serializeAccountUnfrozenEvent((AccountUnfrozenEvent) event);
        } else if (event instanceof AccountClosedEvent) {
            return serializeAccountClosedEvent((AccountClosedEvent) event);
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getName());
    }

    private BaseEvent deserializeEvent(String data, String eventType) throws EventDeserializationException {
        try {
            switch (eventType) {
                case "com.wallet.command.event.AccountCreatedEvent":
                    return deserializeAccountCreatedEvent(data);
                case "com.wallet.command.event.BalanceChangedEvent":
                    return deserializeBalanceChangedEvent(data);
                case "com.wallet.command.event.AccountFrozenEvent":
                    return deserializeAccountFrozenEvent(data);
                case "com.wallet.command.event.AccountUnfrozenEvent":
                    return deserializeAccountUnfrozenEvent(data);
                case "com.wallet.command.event.AccountClosedEvent":
                    return deserializeAccountClosedEvent(data);
                default:
                    throw new EventDeserializationException("Unsupported event type: " + eventType);
            }
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event", e);
        }
    }

    // Implement specific serialization methods for each event type
    private String serializeAccountCreatedEvent(AccountCreatedEvent event) {
        // Implementation
        return Base64.getEncoder().encodeToString(buildAccountCreatedEventData(event).build().toByteArray());
    }

    private String serializeBalanceChangedEvent(BalanceChangedEvent event) {
        // Implementation
        return Base64.getEncoder().encodeToString(buildBalanceChangedEventData(event).build().toByteArray());
    }

    private String serializeAccountFrozenEvent(AccountFrozenEvent event) {
        // Implementation
        return Base64.getEncoder().encodeToString(buildAccountFrozenEventData(event).build().toByteArray());
    }

    private String serializeAccountUnfrozenEvent(AccountUnfrozenEvent event) {
        // Implementation
        return Base64.getEncoder().encodeToString(buildAccountUnfrozenEventData(event).build().toByteArray());
    }

    private String serializeAccountClosedEvent(AccountClosedEvent event) {
        // Implementation
        return Base64.getEncoder().encodeToString(buildAccountClosedEventData(event).build().toByteArray());
    }

    // Implement specific deserialization methods for each event type
    private AccountCreatedEvent deserializeAccountCreatedEvent(String data) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            AccountCreatedEventData message = AccountCreatedEventData.parseFrom(bytes);
            return buildAccountCreatedEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize AccountCreatedEvent", e);
        }
    }

    private BalanceChangedEvent deserializeBalanceChangedEvent(String data) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            BalanceChangedEventData message = BalanceChangedEventData.parseFrom(bytes);
            return buildBalanceChangedEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize BalanceChangedEvent", e);
        }
    }

    private AccountFrozenEvent deserializeAccountFrozenEvent(String data) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            AccountFrozenEventData message = AccountFrozenEventData.parseFrom(bytes);
            return buildAccountFrozenEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize AccountFrozenEvent", e);
        }
    }

    private AccountUnfrozenEvent deserializeAccountUnfrozenEvent(String data) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            AccountUnfrozenEventData message = AccountUnfrozenEventData.parseFrom(bytes);
            return buildAccountUnfrozenEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize AccountUnfrozenEvent", e);
        }
    }

    private AccountClosedEvent deserializeAccountClosedEvent(String data) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            AccountClosedEventData message = AccountClosedEventData.parseFrom(bytes);
            return buildAccountClosedEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize AccountClosedEvent", e);
        }
    }

    private AccountCreatedEventData.Builder buildAccountCreatedEventData(AccountCreatedEvent event) {
        return AccountCreatedEventData.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(event.getTimestamp().toString())
                .setInitialBalance(event.getInitialBalance().toString())
                .setAssetType(event.getAssetType().name())
                .setMinBalance(event.getMinBalance().toString())
                .setMaxBalance(event.getMaxBalance().toString());
    }

    private BalanceChangedEventData.Builder buildBalanceChangedEventData(BalanceChangedEvent event) {
        return BalanceChangedEventData.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(event.getTimestamp().toString())
                .setAmount(event.getAmount().toString())
                .setTransactionId(event.getTransactionId())
                .setTargetAccountId(event.getTargetAccountId());
    }

    private AccountFrozenEventData.Builder buildAccountFrozenEventData(AccountFrozenEvent event) {
        return AccountFrozenEventData.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(event.getTimestamp().toString())
                .setReason(event.getReason());
    }

    private AccountUnfrozenEventData.Builder buildAccountUnfrozenEventData(AccountUnfrozenEvent event) {
        return AccountUnfrozenEventData.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(event.getTimestamp().toString())
                .setReason(event.getReason());
    }

    private AccountClosedEventData.Builder buildAccountClosedEventData(AccountClosedEvent event) {
        return AccountClosedEventData.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(event.getTimestamp().toString())
                .setReason(event.getReason())
                .setFinalBalance(event.getFinalBalance().toString());
    }

    private AccountCreatedEvent buildAccountCreatedEvent(AccountCreatedEventData eventData) {
        return AccountCreatedEvent.builder()
                .eventId(eventData.getEventId())
                .accountId(eventData.getAccountId())
                .operatorId(eventData.getOperatorId())
                .timestamp(Instant.parse(eventData.getTimestamp()))
                .initialBalance(new BigDecimal(eventData.getInitialBalance()))
                .assetType(AssetType.valueOf(eventData.getAssetType()))
                .minBalance(new BigDecimal(eventData.getMinBalance()))
                .maxBalance(new BigDecimal(eventData.getMaxBalance()))
                .build();
    }

    private BalanceChangedEvent buildBalanceChangedEvent(BalanceChangedEventData eventData) {
        return BalanceChangedEvent.builder()
                .eventId(eventData.getEventId())
                .accountId(eventData.getAccountId())
                .operatorId(eventData.getOperatorId())
                .timestamp(Instant.parse(eventData.getTimestamp()))
                .amount(new BigDecimal(eventData.getAmount()))
                .transactionId(eventData.getTransactionId())
                .targetAccountId(eventData.getTargetAccountId())
                .build();
    }

    private AccountFrozenEvent buildAccountFrozenEvent(AccountFrozenEventData eventData) {
        return AccountFrozenEvent.builder()
                .eventId(eventData.getEventId())
                .accountId(eventData.getAccountId())
                .operatorId(eventData.getOperatorId())
                .timestamp(Instant.parse(eventData.getTimestamp()))
                .reason(eventData.getReason())
                .build();
    }

    private AccountUnfrozenEvent buildAccountUnfrozenEvent(AccountUnfrozenEventData eventData) {
        return AccountUnfrozenEvent.builder()
                .eventId(eventData.getEventId())
                .accountId(eventData.getAccountId())
                .operatorId(eventData.getOperatorId())
                .timestamp(Instant.parse(eventData.getTimestamp()))
                .reason(eventData.getReason())
                .build();
    }

    private AccountClosedEvent buildAccountClosedEvent(AccountClosedEventData eventData) {
        return AccountClosedEvent.builder()
                .eventId(eventData.getEventId())
                .accountId(eventData.getAccountId())
                .operatorId(eventData.getOperatorId())
                .timestamp(Instant.parse(eventData.getTimestamp()))
                .reason(eventData.getReason())
                .finalBalance(new BigDecimal(eventData.getFinalBalance()))
                .build();
    }
}
