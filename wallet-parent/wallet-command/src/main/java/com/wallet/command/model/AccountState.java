package com.wallet.command.model;

import com.wallet.command.event.*;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class AccountState {
    private String accountId;
    private AssetType assetType;
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal minBalance = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal maxBalance = BigDecimal.valueOf(Long.MAX_VALUE);
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;
    private long version;
    private Instant lastUpdated;
    private String lastOperatorId;
    private String lastTransactionId;

    public void validateBalanceChange(BigDecimal amount) {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + accountId);
        }
        
        BigDecimal newBalance = this.balance.add(amount);
        if (newBalance.compareTo(this.minBalance) < 0) {
            throw new IllegalArgumentException("Balance would fall below minimum allowed: " + this.minBalance);
        }
        if (newBalance.compareTo(this.maxBalance) > 0) {
            throw new IllegalArgumentException("Balance would exceed maximum allowed: " + this.maxBalance);
        }
    }

    public boolean isFrozen() {
        return this.status == AccountStatus.FROZEN;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public static AccountState fromEvents(List<BaseEvent> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("Events list cannot be null or empty");
        }

        BaseEvent lastEvent = events.get(events.size() - 1);
        AccountStateBuilder builder = AccountState.builder()
                .lastUpdated(lastEvent.getTimestamp())
                .lastOperatorId(lastEvent.getOperatorId());

        // Apply each event in sequence
        for (BaseEvent event : events) {
            if (event instanceof AccountCreatedEvent) {
                applyAccountCreated(builder, (AccountCreatedEvent) event);
            } else if (event instanceof BalanceChangedEvent) {
                applyBalanceChanged(builder, (BalanceChangedEvent) event);
            } else if (event instanceof AccountFrozenEvent) {
                builder.status(AccountStatus.FROZEN);
            } else if (event instanceof AccountUnfrozenEvent) {
                builder.status(AccountStatus.ACTIVE);
            } else if (event instanceof TransferInitiatedEvent) {
                applyTransferInitiated(builder, (TransferInitiatedEvent) event);
            } else if (event instanceof TransferCompletedEvent) {
                applyTransferCompleted(builder, (TransferCompletedEvent) event);
            }
        }

        return builder.build();
    }

    private static void applyAccountCreated(AccountStateBuilder builder, AccountCreatedEvent event) {
        builder.accountId(event.getAccountId())
              .assetType(event.getAssetType())
              .balance(event.getInitialBalance())
              .minBalance(event.getMinBalance())
              .maxBalance(event.getMaxBalance())
              .status(AccountStatus.ACTIVE);
    }

    private static void applyBalanceChanged(AccountStateBuilder builder, BalanceChangedEvent event) {
        AccountState currentState = builder.build();
        builder.balance(currentState.getBalance().add(event.getAmount()))
              .lastTransactionId(event.getTransactionId());
    }

    private static void applyTransferInitiated(AccountStateBuilder builder, TransferInitiatedEvent event) {
        AccountState currentState = builder.build();
        if (event.getAccountId().equals(currentState.getAccountId())) {
            builder.balance(currentState.getBalance().subtract(event.getAmount()));
        } else if (event.getTargetAccountId().equals(currentState.getAccountId())) {
            builder.balance(currentState.getBalance().add(event.getAmount()));
        }
        builder.lastTransactionId(event.getTransactionId());
    }

    private static void applyTransferCompleted(AccountStateBuilder builder, TransferCompletedEvent event) {
        builder.lastTransactionId(event.getTransactionId());
    }
}
