package com.zuhlke.ta.sentiment.pipeline.impl;

public class FatalError extends RuntimeException {
    public FatalError(String message) {
        super(message);
    }

    public FatalError(String message, Throwable cause) {
        super(message, cause);
    }
}
