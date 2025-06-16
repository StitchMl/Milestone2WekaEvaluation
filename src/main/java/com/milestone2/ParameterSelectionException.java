package com.milestone2;

public class ParameterSelectionException extends RuntimeException {
    public ParameterSelectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterSelectionException(String message) {
        super(message);
    }
}