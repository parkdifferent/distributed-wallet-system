package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when a transfer is completed successfully
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class TransferCompletedEvent extends BaseEvent {
    private String targetAccountId;
    private BigDecimal amount;
    private String transactionId;

    public TransferCompletedEvent(String accountId, String operatorId, String targetAccountId,
                                BigDecimal amount, String transactionId) {
        super(accountId, operatorId);
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.transactionId = transactionId;
    }
}
