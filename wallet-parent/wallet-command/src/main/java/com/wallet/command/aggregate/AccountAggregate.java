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

    public boolean canChangeBalance(BigDecimal amount) {
        if (!isActive()) {
            return false;
        }
        BigDecimal newBalance = this.balance.add(amount);
        return newBalance.compareTo(minBalance) >= 0 && newBalance.compareTo(maxBalance) <= 0;
    }

    public boolean canTransfer(BigDecimal amount) {
        return isActive() && canChangeBalance(amount.negate());
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
