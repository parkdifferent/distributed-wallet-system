package com.wallet.command.model.command;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.event.TransferCompletedEvent;
import com.wallet.command.event.TransferInitiatedEvent;
import com.wallet.command.exception.InsufficientBalanceException;
import com.wallet.command.model.AccountState;
import com.wallet.enums.AccountStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to initiate a transfer between two accounts.
 * The source account is specified in the accountId field inherited from Command.
 * The target account is specified in the targetAccountId field.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class TransferCommand extends BaseCommand {
    private String targetAccountId;
    private BigDecimal amount;
    private String transactionId;
    private String description;

    @Override
    public List<BaseEvent> validate(AccountState state) {
        if (state == null) {
            throw new IllegalStateException("Account not found: " + accountId);
        }
        if (state.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active: " + accountId);
        }
        if (targetAccountId == null || targetAccountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Target account ID must not be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (amount.compareTo(state.getBalance()) > 0) {
            throw new InsufficientBalanceException(
                accountId,
                state.getBalance(),
                amount,
                state.getAssetType().name(),
                transactionId
            );
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID must not be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description must not be empty");
        }
        return new ArrayList<>();
    }

    @Override
    public List<BaseEvent> execute(AccountState state) {
        List<BaseEvent> events = new ArrayList<>();
        
        events.add(TransferInitiatedEvent.builder()
            .accountId(accountId)
            .targetAccountId(targetAccountId)
            .amount(amount)
            .operatorId(operatorId)
            .transactionId(transactionId)
            .description(description)
            .timestamp(timestamp)
            .build());
        
        events.add(TransferCompletedEvent.builder()
            .accountId(accountId)
            .targetAccountId(targetAccountId)
            .amount(amount)
            .operatorId(operatorId)
            .transactionId(transactionId)
            .timestamp(timestamp)
            .build());
        
        return events;
    }
}
