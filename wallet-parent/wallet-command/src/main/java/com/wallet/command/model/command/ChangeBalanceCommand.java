package com.wallet.command.model.command;

import com.wallet.command.event.BalanceChangedEvent;
import com.wallet.command.event.BaseEvent;
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
 * Command to change account balance. This command is used for both credit and debit operations.
 * A positive amount indicates a credit operation, while a negative amount indicates a debit operation.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class ChangeBalanceCommand extends BaseCommand {
    private BigDecimal amount;
    private String transactionId;
    private String targetAccountId;

    @Override
    public List<BaseEvent> validate(AccountState state) {
        if (state == null) {
            throw new IllegalStateException("Account not found: " + accountId);
        }
        if (state.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + accountId);
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID must not be empty");
        }
        
        BigDecimal newBalance = state.getBalance().add(amount);
        if (newBalance.compareTo(state.getMinBalance()) < 0) {
            throw new InsufficientBalanceException(
                accountId,
                state.getBalance(),
                amount.abs(),
                state.getAssetType().name(),
                transactionId
            );
        }
        if (newBalance.compareTo(state.getMaxBalance()) > 0) {
            throw new IllegalStateException("Balance would exceed maximum: " + state.getMaxBalance());
        }
        return new ArrayList<>();
    }

    @Override
    public List<BaseEvent> execute(AccountState state) {
        validate(state);
        
        List<BaseEvent> events = new ArrayList<>();
            
        events.add(BalanceChangedEvent.builder()
            .accountId(accountId)
            .amount(amount)
            .operatorId(operatorId)
            .transactionId(transactionId)
            .targetAccountId(targetAccountId)
            .timestamp(timestamp)
            .build());
        return events;
    }
}
