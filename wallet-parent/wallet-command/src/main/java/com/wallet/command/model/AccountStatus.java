package com.wallet.command.model;

/**
 * Account status enum
 */
public enum AccountStatus {
    /**
     * Account is active and can perform transactions
     */
    ACTIVE,
    
    /**
     * Account is frozen and cannot perform transactions
     */
    FROZEN,
    
    /**
     * Account is closed and cannot be used
     */
    CLOSED
}
