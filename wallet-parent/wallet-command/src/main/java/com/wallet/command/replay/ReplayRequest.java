package com.wallet.command.replay;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Request object for replaying events
 */
@Data
@Builder
public class ReplayRequest {
    /**
     * Aggregate ID to replay events for
     */
    private String aggregateId;

    /**
     * Target version to replay events up to
     */
    private Long targetVersion;

    /**
     * Start time for replay
     */
    private Instant startTime;

    /**
     * End time for replay
     */
    private Instant endTime;

    /**
     * Whether to include events from the target version
     */
    private boolean includeTargetVersion;

    /**
     * Whether to include events from the start time
     */
    private boolean includeStartTime;

    /**
     * Whether to include events from the end time
     */
    private boolean includeEndTime;

    /**
     * Validate the replay request
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Aggregate ID cannot be null or empty");
        }

        if (targetVersion != null && targetVersion < 0) {
            throw new IllegalArgumentException("Target version cannot be negative");
        }

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        // At least one filter must be specified
        if (targetVersion == null && startTime == null && endTime == null) {
            throw new IllegalArgumentException("At least one filter (version or time) must be specified");
        }
    }
}
