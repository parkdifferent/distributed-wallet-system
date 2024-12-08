package com.wallet.command.service;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.infrastructure.serialization.EventSerializer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
public class EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final EventSerializer eventSerializer;
    private final String topicName;

    public EventPublisher(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            EventSerializer eventSerializer) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventSerializer = eventSerializer;
        this.topicName = "wallet-events"; // Could be configurable
    }

    public CompletableFuture<Void> publish(List<BaseEvent> events) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (BaseEvent event : events) {
                    String serializedEvent = eventSerializer.serialize(event);
                    byte[] serializedBytes = Base64.getDecoder().decode(serializedEvent);
                    
                    ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                        topicName,
                        event.getAccountId(),  // Use accountId as partition key
                        serializedBytes
                    );
                    
                    // Add headers
                    record.headers().add("eventType", event.getClass().getSimpleName().getBytes());
                    record.headers().add("timestamp", String.valueOf(event.getTimestampMillis()).getBytes());
                    
                    kafkaTemplate.send(record)
                        .addCallback(
                            result -> logger.debug("Published event: {}", event),
                            ex -> logger.error("Failed to publish event: {}", event, ex)
                        );
                }
            } catch (Exception e) {
                logger.error("Error publishing events", e);
                throw new RuntimeException("Failed to publish events", e);
            }
        });
    }
}
