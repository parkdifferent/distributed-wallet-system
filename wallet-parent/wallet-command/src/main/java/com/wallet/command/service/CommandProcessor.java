package com.wallet.command.service;

import com.wallet.command.event.*;
import com.wallet.command.exception.InsufficientBalanceException;
import com.wallet.command.infrastructure.repository.EventStore;
import com.wallet.command.model.command.*;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CommandProcessor {
    private final EventStore eventStore;
    private final EventReplayService eventReplayService;

    public CommandProcessor(EventStore eventStore, EventReplayService eventReplayService) {
        this.eventStore = eventStore;
        this.eventReplayService = eventReplayService;
    }

    public CompletableFuture<List<BaseEvent>> process(Command command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Processing command: {}", command);
                
                // Step 1: Get current version from EventStore
                long currentVersion = eventStore.getCurrentVersion(command.getAccountId()).join();
                
                // Step 2: Load current state
                AccountState accountState = null;
                if (currentVersion >= 0) {
                    accountState = eventReplayService.replayEvents(command.getAccountId()).join();
                }
                
                // Step 3: Validate command
                List<BaseEvent> validationEvents = command.validate(accountState);
                
                // Step 4: Execute command and generate events
                List<BaseEvent> newEvents = command.execute(accountState);
                if (validationEvents != null && !validationEvents.isEmpty()) {
                    newEvents.addAll(0, validationEvents);
                }
                
                // Step 5: Store events with version check
                return eventStore.appendEvents(command.getAccountId(), currentVersion, newEvents)
                    .thenApply(v -> newEvents)
                    .join();
                
            } catch (Exception e) {
                log.error("Failed to process command: {}", command, e);
                throw e;
            }
        });
    }
}
