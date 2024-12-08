package com.wallet.command.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when an account is closed.
 * Contains the final balance of the account at the time of closure.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountClosedEvent extends BaseEvent {
    private BigDecimal finalBalance;
    private String reason;

    public AccountClosedEvent(String accountId, String operatorId, String reason, BigDecimal finalBalance) {
        super(accountId, operatorId);
        this.finalBalance = finalBalance;
        this.reason = reason;
    }
}
