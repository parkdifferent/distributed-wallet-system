package com.wallet.event;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public class AccountFrozenEvent extends AccountStatusEvent {
}