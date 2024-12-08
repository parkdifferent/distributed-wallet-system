package com.wallet.command.model.command;

import com.wallet.command.event.AccountUnfrozenEvent;
import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to unfreeze a frozen account. This will restore the account's ability
 * to perform operations.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class UnfreezeAccountCommand extends BaseCommand {
    private String accountId;
    private String operatorId;
    private String reason;
    private Instant timestamp;

    @Override
    public List<BaseEvent> validate(AccountState state) {
        if (state == null) {
            throw new IllegalStateException("Account not found: " + accountId);
        }
        if (state.getStatus() != AccountStatus.FROZEN) {
            throw new IllegalStateException("Account is not frozen: " + accountId);
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Unfreeze reason must not be empty");
        }
        return new ArrayList<>();
    }

    @Override
    public List<BaseEvent> execute(AccountState state) {
        List<BaseEvent> events = new ArrayList<>();
        events.add(AccountUnfrozenEvent.builder()
            .accountId(accountId)
            .operatorId(operatorId)
            .reason(reason)
            .timestamp(timestamp)
            .build());
        return events;
    }
}
