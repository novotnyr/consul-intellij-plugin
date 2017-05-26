package com.github.novotnyr.idea.consul;

public class ConsulAgentConnectionException extends ConsulException {

    public ConsulAgentConnectionException(Throwable cause) {
        super("Cannot connect to Consul agent", cause);
    }
}