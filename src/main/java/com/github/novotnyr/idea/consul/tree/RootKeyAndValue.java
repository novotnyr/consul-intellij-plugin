package com.github.novotnyr.idea.consul.tree;

public class RootKeyAndValue extends KeyAndValue {
    private String message;

    public RootKeyAndValue() {
        super("");
    }

    @Override
    public String getKey() {
        return "";
    }

    @Override
    public String getFullyQualifiedKey() {
        return "";
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public String toString() {
        return this.message != null ? this.message : "/";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RootKeyAndValue withMessage(String message) {
        setMessage(message);
        return this;
    }
}
