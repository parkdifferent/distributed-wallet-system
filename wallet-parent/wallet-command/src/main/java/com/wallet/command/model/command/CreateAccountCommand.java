package com.wallet.command.model.command;

import com.wallet.command.event.AccountCreatedEvent;
import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AssetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to create a new account
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class CreateAccountCommand extends BaseCommand {
    private String ownerId;
    private String currency;
    private BigDecimal initialBalance;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    private AssetType assetType;

    @Override
    public List<BaseEvent> validate(AccountState state) {
        if (state != null) {
            throw new IllegalStateException("Account already exists: " + accountId);
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must be non-negative");
        }
        if (minBalance == null || maxBalance == null || minBalance.compareTo(maxBalance) > 0) {
            throw new IllegalArgumentException("Min balance must be less than or equal to max balance");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must not be empty");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID must not be empty");
        }
        if (assetType == null) {
            throw new IllegalArgumentException("Asset type must not be null");
        }
        return new ArrayList<>();
    }

    @Override
    public List<BaseEvent> execute(AccountState state) {
        List<BaseEvent> events = new ArrayList<>();
        events.add(AccountCreatedEvent.builder()
                .accountId(accountId)
                .ownerId(ownerId)
                .currency(currency)
                .initialBalance(initialBalance)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .assetType(assetType)
                .operatorId(operatorId)
                .timestamp(getTimestampOrNow())
                .build());
        return events;
    }
}
