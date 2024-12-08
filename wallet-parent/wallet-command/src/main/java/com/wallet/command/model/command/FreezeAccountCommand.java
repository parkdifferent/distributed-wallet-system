package com.wallet.command.model.command;

import com.wallet.command.event.AccountFrozenEvent;
import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to freeze an account when suspicious activity is detected.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class FreezeAccountCommand extends BaseCommand {
    private String accountId;
    private String operatorId;
    private String reason;

    @Override
    public List<BaseEvent> validate(AccountState state) {
        if (state == null) {
            throw new IllegalStateException("Account not found: " + accountId);
        }
        if (state.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + accountId);
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Freeze reason must not be empty");
        }
        return new ArrayList<>();
    }

    @Override
    public List<BaseEvent> execute(AccountState state) {
        List<BaseEvent> events = new ArrayList<>();
        events.add(AccountFrozenEvent.builder()
            .accountId(accountId)
            .operatorId(operatorId)
            .reason(reason)
            .timestamp(getTimestampOrNow())
            .build());
        return events;
    }
}
