package com.wallet.command.event;

import com.wallet.enums.AssetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when a new account is created
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountCreatedEvent extends BaseEvent {
    private String ownerId;
    private String currency;
    private BigDecimal initialBalance;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    private AssetType assetType;

    public AccountCreatedEvent(String accountId, String operatorId, AssetType assetType, 
                             BigDecimal initialBalance, BigDecimal minBalance, BigDecimal maxBalance,
                             String currency, String ownerId) {
        super(accountId, operatorId);
        this.assetType = assetType;
        this.initialBalance = initialBalance;
        this.minBalance = minBalance;
        this.maxBalance = maxBalance;
        this.currency = currency;
        this.ownerId = ownerId;
    }
}
