package com.github.novotnyr.idea.consul.tree;

public class KeyAndValue {
    private String fullyQualifiedKey;

    private String value;

    private boolean isContainer;

    public KeyAndValue(String fullyQualifiedKey) {
        this.fullyQualifiedKey = fullyQualifiedKey;
        if (!this.fullyQualifiedKey.endsWith("/")) {
            this.fullyQualifiedKey += "/";
        }
        this.isContainer = true;
    }


    public KeyAndValue(String fullyQualifiedKey, String value) {
        this.fullyQualifiedKey = fullyQualifiedKey;
        this.value = value;
    }

    public String getFullyQualifiedKey() {
        return fullyQualifiedKey;
    }

    public String getKey() {
        String[] split = this.fullyQualifiedKey.split("/");
        return split[split.length - 1];
    }

    public String getValue() {
        return value;
    }

    public boolean isContainer() {
        return isContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyAndValue)) return false;

        KeyAndValue keyAndValue = (KeyAndValue) o;

        if (isContainer() != keyAndValue.isContainer()) return false;
        if (!getFullyQualifiedKey().equals(keyAndValue.getFullyQualifiedKey())) return false;
        if (!getKey().equals(keyAndValue.getKey())) return false;
        return getValue() != null ? getValue().equals(keyAndValue.getValue()) : keyAndValue.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getFullyQualifiedKey().hashCode();
        result = 31 * result + getKey().hashCode();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (isContainer() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
