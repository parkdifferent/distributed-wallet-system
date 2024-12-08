package com.wallet.command.replay;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AggregateRoot;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object for event replay operations
 */
@Data
@Builder
public class ReplayResult {
    /**
     * The aggregate root after replay
     */
    private AggregateRoot aggregate;

    /**
     * List of successfully applied events
     */
    @Builder.Default
    private List<BaseEvent> appliedEvents = new ArrayList<>();

    /**
     * List of events that failed to apply
     */
    @Builder.Default
    private List<EventError> failedEvents = new ArrayList<>();

    /**
     * Whether the replay operation was successful
     */
    private boolean successful;

    /**
     * Error message if replay failed
     */
    private String errorMessage;

    /**
     * Final version of the aggregate after replay
     */
    private Long finalVersion;

    /**
     * Represents an event that failed to apply during replay
     */
    @Data
    @Builder
    public static class EventError {
        /**
         * The event that failed to apply
         */
        private BaseEvent event;

        /**
         * The error that occurred
         */
        private String error;

        /**
         * The version at which the error occurred
         */
        private Long version;
    }

    /**
     * Add a successfully applied event
     */
    public void addAppliedEvent(BaseEvent event) {
        appliedEvents.add(event);
    }

    /**
     * Add a failed event with error details
     */
    public void addFailedEvent(BaseEvent event, String error, Long version) {
        failedEvents.add(EventError.builder()
            .event(event)
            .error(error)
            .version(version)
            .build());
    }

    /**
     * Get the number of events that were successfully applied
     */
    public int getAppliedEventCount() {
        return appliedEvents.size();
    }

    /**
     * Get the number of events that failed to apply
     */
    public int getFailedEventCount() {
        return failedEvents.size();
    }

    /**
     * Check if any events failed to apply
     */
    public boolean hasFailures() {
        return !failedEvents.isEmpty();
    }
}
