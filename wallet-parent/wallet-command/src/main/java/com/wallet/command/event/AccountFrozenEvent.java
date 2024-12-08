package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event emitted when an account is frozen
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountFrozenEvent extends BaseEvent {
    private String reason;

    public AccountFrozenEvent(String accountId, String operatorId, String reason) {
        super(accountId, operatorId);
        this.reason = reason;
    }
}
