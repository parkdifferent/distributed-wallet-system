package com.wallet.command.model.command;

import com.wallet.command.event.AccountClosedEvent;
import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Command to close an account. The account must have zero balance and must not be frozen.
 * Once closed, the account cannot be reopened and no operations can be performed on it.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CloseAccountCommand implements Command {
    private final String accountId;
    private final String operatorId;
    private final String reason;

    @Override
    public List<BaseEvent> validate(AccountState accountState) {
        if (accountState == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        if (accountState.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }
        if (accountState.getStatus() == AccountStatus.FROZEN) {
            throw new IllegalStateException("Cannot close a frozen account");
        }
        if (accountState.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account must have zero balance before closing");
        }
        return Collections.emptyList();
    }

    @Override
    public List<BaseEvent> execute(AccountState accountState) {
        AccountClosedEvent event = AccountClosedEvent.builder()
                .accountId(accountId)
                .operatorId(operatorId)
                .reason(reason)
                .finalBalance(accountState.getBalance())
                .build();
        return Collections.singletonList(event);
    }
}
