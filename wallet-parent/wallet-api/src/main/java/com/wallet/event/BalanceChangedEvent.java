package com.wallet.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BalanceChangedEvent extends BaseEvent {
    private BigDecimal amount;
    private String transactionId;
}
