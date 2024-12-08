package com.wallet.command.aggregate;

import com.wallet.command.event.*;
import com.wallet.command.model.AggregateRoot;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Account aggregate root that handles all account-related commands and events
 */
@Getter
public class AccountAggregate extends AggregateRoot {
    private AssetType assetType;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    private String currency;
    private String ownerId;

    public AccountAggregate(String accountId) {
        super(accountId);
        this.status = AccountStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

    @Override
    public String getAccountId() {
        return getId();
    }

    @Override
    protected void handleEvent(BaseEvent event) {
        if (event instanceof AccountCreatedEvent) {
            apply((AccountCreatedEvent) event);
        } else if (event instanceof BalanceChangedEvent) {
            apply((BalanceChangedEvent) event);
        } else if (event instanceof AccountFrozenEvent) {
            apply((AccountFrozenEvent) event);
        } else if (event instanceof AccountUnfrozenEvent) {
            apply((AccountUnfrozenEvent) event);
        } else if (event instanceof AccountClosedEvent) {
            apply((AccountClosedEvent) event);
        }
    }

    public void apply(AccountCreatedEvent event) {
        this.assetType = event.getAssetType();
        this.balance = event.getInitialBalance();
        this.minBalance = event.getMinBalance();
        this.maxBalance = event.getMaxBalance();
        this.currency = event.getCurrency();
        this.ownerId = event.getOwnerId();
        this.status = AccountStatus.ACTIVE;
    }

    public void apply(BalanceChangedEvent event) {
        this.balance = this.balance.add(event.getAmount());
    }

    public void apply(AccountFrozenEvent event) {
        this.status = AccountStatus.FROZEN;
    }

    public void apply(AccountUnfrozenEvent event) {
        this.status = AccountStatus.ACTIVE;
    }

    public void apply(AccountClosedEvent event) {
        this.status = AccountStatus.CLOSED;
    }

    /**
     * Validates if a balance change operation can be performed
     * @param amount The amount to change
     * @return true if the operation is valid
     */
    public boolean canChangeBalance(BigDecimal amount) {
        if (!isActive()) {
            throw new IllegalStateException("Account is not active");
        }
        BigDecimal newBalance = this.balance.add(amount);
        if (newBalance.compareTo(minBalance) < 0) {
            throw new IllegalStateException("Balance would fall below minimum: " + minBalance);
        }
        if (newBalance.compareTo(maxBalance) > 0) {
            throw new IllegalStateException("Balance would exceed maximum: " + maxBalance);
        }
        return true;
    }

    /**
     * Validates if a transfer operation can be performed
     * @param target The target account
     * @param amount The amount to transfer
     * @return true if the transfer is valid
     */
    public boolean canTransfer(AccountAggregate target, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }
        if (!target.isActive()) {
            throw new IllegalStateException("Target account is not active");
        }
        if (target.assetType != this.assetType) {
            throw new IllegalStateException("Cannot transfer between different asset types");
        }
        canChangeBalance(amount.negate());
        target.canChangeBalance(amount);
        return true;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean isFrozen() {
        return status == AccountStatus.FROZEN;
    }

    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }
}
