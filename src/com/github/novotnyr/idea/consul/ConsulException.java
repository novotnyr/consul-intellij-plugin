package com.github.novotnyr.idea.consul;

public class ConsulException extends RuntimeException {
    public ConsulException() {
    }

    public ConsulException(String message) {
        super(message);
    }

    public ConsulException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsulException(Throwable cause) {
        super(cause);
    }

    public ConsulException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
