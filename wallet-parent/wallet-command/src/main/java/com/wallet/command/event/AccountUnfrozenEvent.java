package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event emitted when an account is unfrozen
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountUnfrozenEvent extends BaseEvent {
    private String reason;

    public AccountUnfrozenEvent(String accountId, String operatorId, String reason) {
        super(accountId, operatorId);
        this.reason = reason;
    }
}
