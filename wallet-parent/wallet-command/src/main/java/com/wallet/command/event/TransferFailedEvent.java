package com.wallet.command.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when a transfer fails
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferFailedEvent extends BaseEvent {
    private String targetAccountId;
    private BigDecimal amount;
    private String transactionId;
    private String reason;

    public TransferFailedEvent(String accountId, String operatorId, String targetAccountId,
                             BigDecimal amount, String transactionId, String reason) {
        super(accountId, operatorId);
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.reason = reason;
    }
}
