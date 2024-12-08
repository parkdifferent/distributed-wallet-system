package com.wallet.command.infrastructure.serialization;

import com.google.protobuf.Timestamp;
import com.wallet.command.event.*;
import com.wallet.enums.AssetType;
import com.wallet.event.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProtobufEventSerializer implements EventSerializer {

    @Override
    public String serialize(BaseEvent event) {
        return Base64.getEncoder().encodeToString(createEventMessage(event).toByteArray());
    }

    @Override
    public String serializeList(List<BaseEvent> events) {
        EventList.Builder builder = EventList.newBuilder();
        events.forEach(event -> builder.addEvents(createEventMessage(event)));
        return Base64.getEncoder().encodeToString(builder.build().toByteArray());
    }

    @Override
    public BaseEvent deserialize(String data, String eventType) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            EventMessage message = EventMessage.parseFrom(bytes);
            return convertToEvent(message);
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event", e);
        }
    }

    @Override
    public List<BaseEvent> deserializeList(String data, List<String> eventTypes) throws EventDeserializationException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            EventList eventList = EventList.parseFrom(bytes);
            return eventList.getEventsList().stream()
                    .map(this::convertToEvent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventDeserializationException("Failed to deserialize event list", e);
        }
    }

    private EventMessage createEventMessage(BaseEvent event) {
        EventMessage.Builder builder = EventMessage.newBuilder()
                .setEventId(event.getEventId())
                .setAccountId(event.getAccountId())
                .setOperatorId(event.getOperatorId())
                .setTimestamp(instantToTimestamp(event.getTimestamp()));

        if (event instanceof AccountCreatedEvent) {
            builder.setAccountCreated(createAccountCreatedData((AccountCreatedEvent) event));
        } else if (event instanceof BalanceChangedEvent) {
            builder.setBalanceChanged(createBalanceChangedData((BalanceChangedEvent) event));
        } else if (event instanceof AccountFrozenEvent) {
            builder.setAccountFrozen(createAccountFrozenData((AccountFrozenEvent) event));
        } else if (event instanceof AccountUnfrozenEvent) {
            builder.setAccountUnfrozen(createAccountUnfrozenData((AccountUnfrozenEvent) event));
        } else if (event instanceof AccountClosedEvent) {
            builder.setAccountClosed(createAccountClosedData((AccountClosedEvent) event));
        }

        return builder.build();
    }

    private AccountCreatedEventData createAccountCreatedData(AccountCreatedEvent event) {
        return AccountCreatedEventData.newBuilder()
                .setInitialBalance(event.getInitialBalance().toString())
                .setAssetType(event.getAssetType().name())
                .setMinBalance(event.getMinBalance().toString())
                .setMaxBalance(event.getMaxBalance().toString())
                .build();
    }

    private BalanceChangedEventData createBalanceChangedData(BalanceChangedEvent event) {
        return BalanceChangedEventData.newBuilder()
                .setAmount(event.getAmount().toString())
                .setTransactionId(event.getTransactionId())
                .setTargetAccountId(event.getTargetAccountId())
                .build();
    }

    private AccountFrozenEventData createAccountFrozenData(AccountFrozenEvent event) {
        return AccountFrozenEventData.newBuilder()
                .setReason(event.getReason())
                .build();
    }

    private AccountUnfrozenEventData createAccountUnfrozenData(AccountUnfrozenEvent event) {
        return AccountUnfrozenEventData.newBuilder()
                .setReason(event.getReason())
                .build();
    }

    private AccountClosedEventData createAccountClosedData(AccountClosedEvent event) {
        return AccountClosedEventData.newBuilder()
                .setReason(event.getReason())
                .build();
    }

    private BaseEvent convertToEvent(EventMessage message) {
        switch (message.getEventDataCase()) {
            case ACCOUNT_CREATED:
                return createAccountCreatedEvent(message);
            case BALANCE_CHANGED:
                return createBalanceChangedEvent(message);
            case ACCOUNT_FROZEN:
                return createAccountFrozenEvent(message);
            case ACCOUNT_UNFROZEN:
                return createAccountUnfrozenEvent(message);
            case ACCOUNT_CLOSED:
                return createAccountClosedEvent(message);
            default:
                throw new IllegalArgumentException("Unknown event type: " + message.getEventDataCase());
        }
    }

    private AccountCreatedEvent createAccountCreatedEvent(EventMessage message) {
        AccountCreatedEventData data = message.getAccountCreated();
        return AccountCreatedEvent.builder()
                .eventId(message.getEventId())
                .accountId(message.getAccountId())
                .operatorId(message.getOperatorId())
                .timestamp(timestampToInstant(message.getTimestamp()))
                .initialBalance(new BigDecimal(data.getInitialBalance()))
                .assetType(AssetType.valueOf(data.getAssetType()))
                .minBalance(new BigDecimal(data.getMinBalance()))
                .maxBalance(new BigDecimal(data.getMaxBalance()))
                .build();
    }

    private BalanceChangedEvent createBalanceChangedEvent(EventMessage message) {
        BalanceChangedEventData data = message.getBalanceChanged();
        return BalanceChangedEvent.builder()
                .eventId(message.getEventId())
                .accountId(message.getAccountId())
                .operatorId(message.getOperatorId())
                .timestamp(timestampToInstant(message.getTimestamp()))
                .amount(new BigDecimal(data.getAmount()))
                .transactionId(data.getTransactionId())
                .targetAccountId(data.getTargetAccountId())
                .build();
    }

    private AccountFrozenEvent createAccountFrozenEvent(EventMessage message) {
        AccountFrozenEventData data = message.getAccountFrozen();
        return AccountFrozenEvent.builder()
                .eventId(message.getEventId())
                .accountId(message.getAccountId())
                .operatorId(message.getOperatorId())
                .timestamp(timestampToInstant(message.getTimestamp()))
                .reason(data.getReason())
                .build();
    }

    private AccountUnfrozenEvent createAccountUnfrozenEvent(EventMessage message) {
        AccountUnfrozenEventData data = message.getAccountUnfrozen();
        return AccountUnfrozenEvent.builder()
                .eventId(message.getEventId())
                .accountId(message.getAccountId())
                .operatorId(message.getOperatorId())
                .timestamp(timestampToInstant(message.getTimestamp()))
                .reason(data.getReason())
                .build();
    }

    private AccountClosedEvent createAccountClosedEvent(EventMessage message) {
        AccountClosedEventData data = message.getAccountClosed();
        return AccountClosedEvent.builder()
                .eventId(message.getEventId())
                .accountId(message.getAccountId())
                .operatorId(message.getOperatorId())
                .timestamp(timestampToInstant(message.getTimestamp()))
                .reason(data.getReason())
                .build();
    }

    private Timestamp instantToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private Instant timestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
