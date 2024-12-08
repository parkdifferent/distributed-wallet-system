package com.wallet.command.exception;

import java.math.BigDecimal;

/**
 * 余额不足异常
 */
public class InsufficientBalanceException extends RuntimeException {
    private final String accountId;
    private final BigDecimal currentBalance;
    private final BigDecimal requestedAmount;
    private final String currency;
    private final String transactionId;

    public InsufficientBalanceException(String accountId, BigDecimal currentBalance, 
                                      BigDecimal requestedAmount, String currency,
                                      String transactionId) {
        super(String.format("Insufficient balance in account %s. Current balance: %s %s, Requested amount: %s %s, Transaction ID: %s",
                accountId, currentBalance, currency, requestedAmount, currency, transactionId));
        this.accountId = accountId;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
        this.currency = currency;
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
