package com.zephsie.report.utils.exceptions;

public class IllegalParamValuesException extends RuntimeException {
    public IllegalParamValuesException() {
        super();
    }

    public IllegalParamValuesException(String message) {
        super(message);
    }

    public IllegalParamValuesException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParamValuesException(Throwable cause) {
        super(cause);
    }

    protected IllegalParamValuesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}