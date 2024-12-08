package com.wallet.command.exception;

/**
 * 钱包系统基础异常类
 */
public class WalletException extends RuntimeException {
    
    public WalletException(String message) {
        super(message);
    }
    
    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }
}
