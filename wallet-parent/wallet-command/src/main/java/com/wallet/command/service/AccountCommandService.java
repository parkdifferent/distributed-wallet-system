package com.wallet.command.service;

import com.wallet.command.model.AccountState;
import com.wallet.enums.AssetType;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Account Command Service Interface
 * Handles all account-related commands in an event-sourced manner
 */
public interface AccountCommandService {
    /**
     * Create a new account
     *
     * @param accountId Account identifier
     * @param assetType Type of asset (e.g., FIAT, CRYPTO)
     * @param initialBalance Initial balance for the account
     * @param minBalance Minimum allowed balance
     * @param maxBalance Maximum allowed balance
     * @param operatorId ID of the operator performing the action
     * @return Future containing the account state
     */
    CompletableFuture<AccountState> createAccount(String accountId, 
                                                AssetType assetType, 
                                                BigDecimal initialBalance,
                                                BigDecimal minBalance,
                                                BigDecimal maxBalance,
                                                String operatorId);
    
    /**
     * Change account balance
     *
     * @param accountId Account identifier
     * @param amount Amount to change (positive for credit, negative for debit)
     * @param transactionId Unique transaction identifier
     * @param operatorId ID of the operator performing the action
     * @return Future containing the account state
     */
    CompletableFuture<AccountState> changeBalance(String accountId, 
                                               BigDecimal amount, 
                                               String transactionId,
                                               String operatorId);
    
    /**
     * Freeze account
     *
     * @param accountId Account identifier
     * @param reason Reason for freezing the account
     * @param operatorId ID of the operator performing the action
     * @return Future containing the account state
     */
    CompletableFuture<AccountState> freezeAccount(String accountId, 
                                               String reason,
                                               String operatorId);
    
    /**
     * Unfreeze account
     *
     * @param accountId Account identifier
     * @param reason Reason for unfreezing the account
     * @param operatorId ID of the operator performing the action
     * @return Future containing the account state
     */
    CompletableFuture<AccountState> unfreezeAccount(String accountId, 
                                                 String reason,
                                                 String operatorId);
    
    /**
     * Transfer between accounts
     *
     * @param sourceAccountId Source account identifier
     * @param targetAccountId Target account identifier
     * @param amount Amount to transfer
     * @param transactionId Unique transaction identifier
     * @param operatorId ID of the operator performing the action
     * @return Future containing the source account state
     */
    CompletableFuture<AccountState> transfer(String sourceAccountId, 
                                         String targetAccountId, 
                                         BigDecimal amount, 
                                         String transactionId,
                                         String operatorId);
}
