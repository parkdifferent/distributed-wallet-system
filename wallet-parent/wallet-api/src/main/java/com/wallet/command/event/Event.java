package com.wallet.command.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all events in the wallet system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Event {
    private String eventId;
    private String aggregateId;
    private long version;
    private long timestamp;
}
