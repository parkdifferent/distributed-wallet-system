package com.wallet.command.service.impl;

import com.wallet.command.event.*;
import com.wallet.command.infrastructure.consensus.RaftConsensusManager;
import com.wallet.command.model.AccountState;
import com.wallet.command.model.command.*;
import com.wallet.command.service.AccountCommandService;
import com.wallet.enums.AssetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountCommandServiceImpl implements AccountCommandService {
    private final RaftConsensusManager consensusManager;

    @Override
    public CompletableFuture<AccountState> createAccount(String accountId, 
                                                      AssetType assetType, 
                                                      BigDecimal initialBalance,
                                                      BigDecimal minBalance,
                                                      BigDecimal maxBalance,
                                                      String operatorId) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountId(accountId)
                .assetType(assetType)
                .initialBalance(initialBalance)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .operatorId(operatorId)
                .build();

        return consensusManager.submitCommand(command)
                .thenApply(events -> {
                    if (events == null || events.isEmpty()) {
                        throw new IllegalStateException("No events generated for command: " + command);
                    }
                    return AccountState.fromEvents(events);
                });
    }

    @Override
    public CompletableFuture<AccountState> changeBalance(String accountId, 
                                                      BigDecimal amount,
                                                      String transactionId,
                                                      String operatorId) {
        ChangeBalanceCommand command = ChangeBalanceCommand.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionId(transactionId)
                .operatorId(operatorId)
                .build();

        return consensusManager.submitCommand(command)
                .thenApply(events -> {
                    if (events == null || events.isEmpty()) {
                        throw new IllegalStateException("No events generated for command: " + command);
                    }
                    return AccountState.fromEvents(events);
                });
    }

    @Override
    public CompletableFuture<AccountState> transfer(String sourceAccountId,
                                                 String targetAccountId,
                                                 BigDecimal amount,
                                                 String transactionId,
                                                 String operatorId) {
        TransferCommand command = TransferCommand.builder()
                .accountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .transactionId(transactionId)
                .operatorId(operatorId)
                .build();

        return consensusManager.submitCommand(command)
                .thenApply(events -> {
                    if (events == null || events.isEmpty()) {
                        throw new IllegalStateException("No events generated for command: " + command);
                    }
                    return AccountState.fromEvents(events);
                });
    }

    @Override
    public CompletableFuture<AccountState> freezeAccount(String accountId,
                                                      String reason,
                                                      String operatorId) {
        FreezeAccountCommand command = FreezeAccountCommand.builder()
                .accountId(accountId)
                .reason(reason)
                .operatorId(operatorId)
                .build();

        return consensusManager.submitCommand(command)
                .thenApply(events -> {
                    if (events == null || events.isEmpty()) {
                        throw new IllegalStateException("No events generated for command: " + command);
                    }
                    return AccountState.fromEvents(events);
                });
    }

    @Override
    public CompletableFuture<AccountState> unfreezeAccount(String accountId,
                                                        String reason,
                                                        String operatorId) {
        UnfreezeAccountCommand command = UnfreezeAccountCommand.builder()
                .accountId(accountId)
                .reason(reason)
                .operatorId(operatorId)
                .build();

        return consensusManager.submitCommand(command)
                .thenApply(events -> {
                    if (events == null || events.isEmpty()) {
                        throw new IllegalStateException("No events generated for command: " + command);
                    }
                    return AccountState.fromEvents(events);
                });
    }
}
