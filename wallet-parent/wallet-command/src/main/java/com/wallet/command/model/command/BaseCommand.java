package com.wallet.command.model.command;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseCommand implements Command {
    protected String commandId;
    protected String accountId;
    protected String operatorId;
    protected Instant timestamp;

    protected BaseCommand(String accountId, String operatorId) {
        this.commandId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.operatorId = operatorId;
        this.timestamp = Instant.now();
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    protected String getCommandIdOrGenerate() {
        if (commandId == null) {
            commandId = UUID.randomUUID().toString();
        }
        return commandId;
    }

    protected Instant getTimestampOrNow() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        return timestamp;
    }

    @Override
    public abstract List<BaseEvent> validate(AccountState state);

    @Override
    public abstract List<BaseEvent> execute(AccountState state);
}
