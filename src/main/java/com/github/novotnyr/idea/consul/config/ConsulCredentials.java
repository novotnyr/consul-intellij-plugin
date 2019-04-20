package com.github.novotnyr.idea.consul.config;

import java.util.Optional;

public class ConsulCredentials {
    private String user;

    private String password;

    private String aclToken;

    public Optional<String> user() {
        return Optional.ofNullable(user);
    }

    public Optional<String> password() {
        return Optional.ofNullable(password);
    }

    public Optional<String> aclToken() {
        return Optional.ofNullable(aclToken);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getAclToken() {
        return aclToken;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAclToken(String aclToken) {
        this.aclToken = aclToken;
    }
}
