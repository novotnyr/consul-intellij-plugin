package com.github.novotnyr.idea.consul.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class KVNode extends DefaultMutableTreeNode {
    public KVNode(KeyAndValue keyAndValue) {
        super(keyAndValue);
    }

    @Override
    public KeyAndValue getUserObject() {
        return (KeyAndValue) super.getUserObject();
    }

    public KeyAndValue getKeyAndValue() {
        return getUserObject();
    }

    public void setKeyAndValue(KeyAndValue keyAndValue) {
        setUserObject(keyAndValue);
    }
}
