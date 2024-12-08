package com.wallet.command.exception;

/**
 * Exception thrown when attempting to perform an operation on an account with invalid status
 */
public class InvalidAccountStatusException extends RuntimeException {
    public InvalidAccountStatusException(String message) {
        super(message);
    }

    public InvalidAccountStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
