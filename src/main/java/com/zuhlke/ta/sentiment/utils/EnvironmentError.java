package com.zuhlke.ta.sentiment.utils;

public class EnvironmentError extends RuntimeException {
    public EnvironmentError(String message, Throwable cause) {
        super(message, cause);
    }
}
