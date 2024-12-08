package com.wallet.command.model.command;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.model.AccountState;

import java.util.List;

public interface Command {
    String getAccountId();
    List<BaseEvent> validate(AccountState accountState);
    List<BaseEvent> execute(AccountState accountState);
}
