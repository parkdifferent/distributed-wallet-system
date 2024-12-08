package com.wallet.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEvent {
    protected String accountId;
    protected String operatorId;
    protected Instant timestamp;
}
