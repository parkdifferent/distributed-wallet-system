package com.wallet.command.service;

import com.wallet.enums.AssetType;
import com.wallet.command.model.AccountState;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for wallet command operations.
 */
public interface WalletCommandService {
    /**
     * Creates a new account.
     *
     * @param accountId The unique identifier for the account
     * @param initialBalance The initial balance for the account
     * @param assetType The type of asset for the account
     * @param minBalance The minimum allowed balance
     * @param maxBalance The maximum allowed balance
     * @param operatorId The operator creating the account
     * @return A future that completes with the account ID
     */
    CompletableFuture<String> createAccount(
        String accountId,
        BigDecimal initialBalance,
        AssetType assetType,
        BigDecimal minBalance,
        BigDecimal maxBalance,
        String operatorId
    );

    /**
     * Transfers balance between accounts.
     *
     * @param fromAccountId The source account identifier
     * @param toAccountId The target account identifier
     * @param amount The amount to transfer
     * @param operatorId The operator performing the transfer
     * @param dedupId The unique deduplication identifier
     * @return A future that completes when the operation is done
     */
    CompletableFuture<Void> transfer(
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String operatorId,
        String dedupId
    );

    /**
     * Changes the balance of an account.
     *
     * @param accountId The account identifier
     * @param amount The amount to change (positive for increase, negative for decrease)
     * @param operatorId The operator performing the change
     * @param dedupId The unique deduplication identifier
     * @return A future that completes when the operation is done
     */
    CompletableFuture<Void> changeBalance(
        String accountId,
        BigDecimal amount,
        String operatorId,
        String dedupId
    );

    /**
     * Freezes an account.
     *
     * @param accountId The account identifier
     * @param operatorId The ID of the operator performing the freeze
     * @param reason The reason for freezing the account
     * @return A future that completes when the operation is done
     */
    CompletableFuture<Void> freezeAccount(
        String accountId,
        String operatorId,
        String reason
    );

    /**
     * Unfreezes an account.
     *
     * @param accountId The account identifier
     * @param operatorId The ID of the operator performing the unfreeze
     * @param reason The reason for unfreezing the account
     * @return A future that completes when the operation is done
     */
    CompletableFuture<Void> unfreezeAccount(
        String accountId,
        String operatorId,
        String reason
    );

    /**
     * Gets the current state of an account.
     *
     * @param accountId The account identifier
     * @return A future that completes with the account state
     */
    CompletableFuture<Optional<AccountState>> getAccountState(String accountId);
}
