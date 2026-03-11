package com.test.btg.exception;

public class FundAlreadySubscribedException extends RuntimeException {
    public FundAlreadySubscribedException(String message) {
        super(message);
    }

    public FundAlreadySubscribedException(String message, Throwable cause) {
        super(message, cause);
    }
}

