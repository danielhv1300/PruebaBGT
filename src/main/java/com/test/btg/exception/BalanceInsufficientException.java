package com.test.btg.exception;

public class BalanceInsufficientException extends RuntimeException {
    
    public BalanceInsufficientException(String message) {
        super(message);
    }

    public BalanceInsufficientException(String message, Throwable cause) {
        super(message, cause);
    }
}

