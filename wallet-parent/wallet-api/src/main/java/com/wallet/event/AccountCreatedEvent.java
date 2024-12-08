package com.wallet.event;

import com.wallet.enums.AssetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountCreatedEvent extends BaseEvent {
    private String ownerId;
    private AssetType assetType;
    private BigDecimal initialBalance;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    private String currency;
}
