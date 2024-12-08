package com.wallet.command.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when a transfer between accounts is initiated.
 * Contains source account, target account, amount and transaction details.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class TransferInitiatedEvent extends BaseEvent {
    private String targetAccountId;
    private BigDecimal amount;
    private String transactionId;
    private String description;
}
