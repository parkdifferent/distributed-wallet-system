package com.wallet.command.infrastructure.repository;

import com.wallet.command.event.BaseEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventStore {
    /**
     * Append new events to the event store
     *
     * @param aggregateId Aggregate ID
     * @param expectedVersion Expected version before appending
     * @param events Events to append
     * @return CompletableFuture of success/failure
     */
    CompletableFuture<Void> appendEvents(String aggregateId, long expectedVersion, List<BaseEvent> events);

    /**
     * Read events for an aggregate from a specific version
     *
     * @param aggregateId Aggregate ID
     * @param fromVersion Version to start reading from
     * @return CompletableFuture of event list
     */
    CompletableFuture<List<BaseEvent>> readEvents(String aggregateId, long fromVersion);

    /**
     * Read all events for an aggregate
     *
     * @param aggregateId Aggregate ID
     * @return CompletableFuture of event list
     */
    CompletableFuture<List<BaseEvent>> readAllEvents(String aggregateId);

    /**
     * Get the current version for an aggregate
     *
     * @param aggregateId Aggregate ID
     * @return CompletableFuture of current version
     */
    CompletableFuture<Long> getCurrentVersion(String aggregateId);
}
