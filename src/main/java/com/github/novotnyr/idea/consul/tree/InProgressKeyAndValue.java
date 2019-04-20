package com.github.novotnyr.idea.consul.tree;

public class InProgressKeyAndValue extends KeyAndValue {
    public InProgressKeyAndValue() {
        super("", "Loading...");
    }

    @Override
    public boolean isContainer() {
        return true;
    }
}
