package com.github.novotnyr.idea.consul;

public class UnauthorizedConsulAccessException extends ConsulException {
    public UnauthorizedConsulAccessException(Throwable cause) {
        super("Unauthorized access to Consul", cause);
    }
}