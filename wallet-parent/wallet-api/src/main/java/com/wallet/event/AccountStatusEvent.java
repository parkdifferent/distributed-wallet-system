package com.wallet.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class AccountStatusEvent extends BaseEvent {
    protected String reason;
}
