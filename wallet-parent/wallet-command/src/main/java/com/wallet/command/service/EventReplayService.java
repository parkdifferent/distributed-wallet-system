package com.wallet.command.service;

import com.wallet.command.event.*;
import com.wallet.command.infrastructure.repository.EventStore;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EventReplayService {
    private static final String METRIC_EVENT_REPLAY = "wallet.event.replay";
    
    private final EventStore eventStore;
    private final MeterRegistry meterRegistry;

    @Autowired
    public EventReplayService(EventStore eventStore, MeterRegistry meterRegistry) {
        this.eventStore = eventStore;
        this.meterRegistry = meterRegistry;
    }

    @Transactional(readOnly = true)
    public CompletableFuture<AccountState> replayEvents(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<BaseEvent> events = eventStore.readAllEvents(accountId).join();
                if (events.isEmpty()) {
                    throw new RuntimeException("No events found for account: " + accountId);
                }

                AccountState accountState = AccountState.fromEvents(events);
                long eventCount = events.size();

                log.debug("Replayed {} events for account {}", eventCount, accountId);
                meterRegistry.counter(METRIC_EVENT_REPLAY,
                    "accountId", accountId,
                    "status", "success",
                    "eventCount", String.valueOf(eventCount))
                    .increment();

                return accountState;

            } catch (Exception e) {
                log.error("Failed to replay events for account: {}", accountId, e);
                meterRegistry.counter(METRIC_EVENT_REPLAY,
                    "accountId", accountId,
                    "status", "error",
                    "error", e.getClass().getSimpleName())
                    .increment();
                throw new RuntimeException("Failed to replay events", e);
            }
        });
    }

    @Transactional(readOnly = true)
    public CompletableFuture<AccountState> replayEventsFromSnapshot(String accountId, Long snapshotVersion) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get events after snapshot
                List<BaseEvent> events = eventStore.readEvents(accountId, snapshotVersion).join();
                if (events.isEmpty()) {
                    throw new RuntimeException("No events found for account: " + accountId);
                }

                AccountState accountState = AccountState.fromEvents(events);
                long eventCount = events.size();

                log.debug("Replayed {} events from snapshot for account {}", eventCount, accountId);
                meterRegistry.counter(METRIC_EVENT_REPLAY,
                    "accountId", accountId,
                    "status", "success_from_snapshot",
                    "eventCount", String.valueOf(eventCount))
                    .increment();

                return accountState;

            } catch (Exception e) {
                log.error("Failed to replay events from snapshot for account: {}", accountId, e);
                meterRegistry.counter(METRIC_EVENT_REPLAY,
                    "accountId", accountId,
                    "status", "error_from_snapshot",
                    "error", e.getClass().getSimpleName())
                    .increment();
                throw new RuntimeException("Failed to replay events from snapshot", e);
            }
        });
    }
}
