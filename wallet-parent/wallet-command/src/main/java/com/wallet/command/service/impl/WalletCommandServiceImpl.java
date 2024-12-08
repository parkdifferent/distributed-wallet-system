package com.wallet.command.service.impl;

import com.wallet.command.event.*;
import com.wallet.command.infrastructure.repository.EventStore;
import com.wallet.command.model.AccountState;
import com.wallet.command.model.command.*;
import com.wallet.command.service.CommandProcessor;
import com.wallet.command.service.EventReplayService;
import com.wallet.command.service.WalletCommandService;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import com.wallet.enums.TransactionType;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WalletCommandServiceImpl implements WalletCommandService {
    private static final String METRIC_COMMAND = "wallet.command";
    private static final String METRIC_TRANSFER = "wallet.transfer";
    
    private final EventStore eventStore;
    private final CommandProcessor commandProcessor;
    private final EventReplayService eventReplayService;
    private final MeterRegistry meterRegistry;

    public WalletCommandServiceImpl(
            EventStore eventStore,
            CommandProcessor commandProcessor,
            EventReplayService eventReplayService,
            MeterRegistry meterRegistry) {
        this.eventStore = eventStore;
        this.commandProcessor = commandProcessor;
        this.eventReplayService = eventReplayService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Transactional
    public CompletableFuture<String> createAccount(String accountId, BigDecimal initialBalance, 
            AssetType assetType, BigDecimal minBalance, BigDecimal maxBalance, String operatorId) {
        log.info("Creating account with ID: {}, assetType: {}, initial balance: {}", 
            accountId, assetType, initialBalance);
        
        meterRegistry.counter(METRIC_COMMAND, "type", "create_account").increment();

        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountId(accountId)
                .assetType(assetType)
                .initialBalance(initialBalance)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .operatorId(operatorId)
                .timestamp(Instant.now())
                .build();

        return commandProcessor.process(command)
                .thenApply(events -> accountId);
    }

    @Override
    @Transactional
    public CompletableFuture<Void> transfer(String sourceAccountId, String targetAccountId, 
            BigDecimal amount, String operatorId, String transactionId) {
        log.info("Initiating transfer from {} to {}, amount: {}, txId: {}", 
            sourceAccountId, targetAccountId, amount, transactionId);
        
        meterRegistry.counter(METRIC_TRANSFER).increment();

        return CompletableFuture.allOf(
                eventReplayService.replayEvents(sourceAccountId),
                eventReplayService.replayEvents(targetAccountId))
            .thenCompose(v -> {
                AccountState sourceAccount = eventReplayService.replayEvents(sourceAccountId).join();
                AccountState targetAccount = eventReplayService.replayEvents(targetAccountId).join();

                validateTransfer(sourceAccount, targetAccount, amount);

                TransferCommand command = TransferCommand.builder()
                        .accountId(sourceAccountId)
                        .targetAccountId(targetAccountId)
                        .amount(amount)
                        .transactionId(transactionId)
                        .operatorId(operatorId)
                        .timestamp(Instant.now())
                        .build();

                return commandProcessor.process(command);
            })
            .thenAccept(events -> {});
    }

    @Override
    @Transactional
    public CompletableFuture<Void> changeBalance(String accountId, BigDecimal amount, 
            String operatorId, String transactionId) {
        log.info("Changing balance for account: {}, amount: {}, txId: {}", 
            accountId, amount, transactionId);
        
        meterRegistry.counter(METRIC_COMMAND, "type", "change_balance").increment();

        return eventReplayService.replayEvents(accountId)
            .thenCompose(account -> {
                validateBalanceChange(account, amount);

                ChangeBalanceCommand command = ChangeBalanceCommand.builder()
                        .accountId(accountId)
                        .amount(amount)
                        .transactionId(transactionId)
                        .operatorId(operatorId)
                        .timestamp(Instant.now())
                        .build();

                return commandProcessor.process(command);
            })
            .thenAccept(events -> {});
    }

    @Override
    @Transactional
    public CompletableFuture<Void> freezeAccount(String accountId, String operatorId, String reason) {
        log.info("Freezing account: {}, reason: {}", accountId, reason);
        
        meterRegistry.counter(METRIC_COMMAND, "type", "freeze_account").increment();

        return eventReplayService.replayEvents(accountId)
            .thenCompose(account -> {
                if (account.getStatus() == AccountStatus.FROZEN) {
                    throw new IllegalStateException("Account is already frozen");
                }

                FreezeAccountCommand command = FreezeAccountCommand.builder()
                        .accountId(accountId)
                        .operatorId(operatorId)
                        .reason(reason)
                        .timestamp(Instant.now())
                        .build();

                return commandProcessor.process(command);
            })
            .thenAccept(events -> {});
    }

    @Override
    @Transactional
    public CompletableFuture<Void> unfreezeAccount(String accountId, String operatorId, String reason) {
        log.info("Unfreezing account: {}, reason: {}", accountId, reason);
        
        meterRegistry.counter(METRIC_COMMAND, "type", "unfreeze_account").increment();

        return eventReplayService.replayEvents(accountId)
            .thenCompose(account -> {
                if (account.getStatus() != AccountStatus.FROZEN) {
                    throw new IllegalStateException("Account is not frozen");
                }

                UnfreezeAccountCommand command = UnfreezeAccountCommand.builder()
                        .accountId(accountId)
                        .operatorId(operatorId)
                        .reason(reason)
                        .timestamp(Instant.now())
                        .build();

                return commandProcessor.process(command);
            })
            .thenAccept(events -> {});
    }

    @Override
    public CompletableFuture<Optional<AccountState>> getAccountState(String accountId) {
        return eventReplayService.replayEvents(accountId)
            .thenApply(Optional::of)
            .exceptionally(e -> Optional.empty());
    }

    private void validateTransfer(AccountState sourceAccount, AccountState targetAccount, BigDecimal amount) {
        if (sourceAccount == null) {
            throw new IllegalStateException("Source account does not exist");
        }
        if (targetAccount == null) {
            throw new IllegalStateException("Target account does not exist");
        }
        if (sourceAccount.getAccountId().equals(targetAccount.getAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active");
        }
        if (targetAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Target account is not active");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (sourceAccount.getBalance().subtract(amount).compareTo(sourceAccount.getMinBalance()) < 0) {
            throw new IllegalStateException("Insufficient balance for transfer");
        }
        if (targetAccount.getBalance().add(amount).compareTo(targetAccount.getMaxBalance()) > 0) {
            throw new IllegalStateException("Transfer would exceed target account maximum balance");
        }
        if (!sourceAccount.getAssetType().equals(targetAccount.getAssetType())) {
            throw new IllegalStateException("Cannot transfer between accounts with different asset types");
        }
    }

    private void validateBalanceChange(AccountState account, BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        
        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(account.getMinBalance()) < 0) {
            throw new IllegalStateException("Balance change would result in balance below minimum");
        }
        if (newBalance.compareTo(account.getMaxBalance()) > 0) {
            throw new IllegalStateException("Balance change would result in balance above maximum");
        }
    }
}
