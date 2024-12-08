package com.wallet.command.model;

import com.wallet.command.event.BaseEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all aggregate roots in the domain
 */
@Getter
public abstract class AggregateRoot {
    private final String id;
    private Long version;
    private final List<BaseEvent> uncommittedChanges;

    protected AggregateRoot(String id) {
        this.id = id;
        this.version = -1L;
        this.uncommittedChanges = new ArrayList<>();
    }

    /**
     * Get account ID (alias for id)
     */
    public String getAccountId() {
        return this.id;
    }

    /**
     * Get the ID of this aggregate
     */
    public String getId() {
        return id;
    }

    /**
     * Get the current version of the aggregate
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Apply an event to the aggregate
     */
    public void apply(BaseEvent event) {
        handleEvent(event);
        uncommittedChanges.add(event);
    }

    /**
     * Handle a specific event type
     */
    protected abstract void handleEvent(BaseEvent event);

    /**
     * Increment the version of the aggregate
     */
    protected void incrementVersion() {
        this.version++;
    }

    /**
     * Get all uncommitted changes
     */
    public List<BaseEvent> getUncommittedChanges() {
        return new ArrayList<>(uncommittedChanges);
    }

    /**
     * Mark all changes as committed
     */
    public void markChangesAsCommitted() {
        this.uncommittedChanges.clear();
    }

    /**
     * Load from history
     */
    public void loadFromHistory(List<BaseEvent> history) {
        history.forEach(event -> {
            handleEvent(event);
            incrementVersion();
        });
    }
}
