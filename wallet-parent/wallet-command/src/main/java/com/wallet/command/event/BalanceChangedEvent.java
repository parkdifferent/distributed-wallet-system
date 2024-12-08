package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when an account's balance changes
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class BalanceChangedEvent extends BaseEvent {
    private BigDecimal amount;
    private String transactionId;
    private String targetAccountId;

    public BalanceChangedEvent(String accountId, String operatorId, BigDecimal amount,
                             String transactionId, String targetAccountId) {
        super(accountId, operatorId);
        this.amount = amount;
        this.transactionId = transactionId;
        this.targetAccountId = targetAccountId;
    }
}
