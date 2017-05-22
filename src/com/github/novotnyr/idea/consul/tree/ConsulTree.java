package com.github.novotnyr.idea.consul.tree;

import com.intellij.ui.treeStructure.Tree;

public class ConsulTree extends Tree {
    public ConsulTreeModel getConsulTreeModel() {
        return (ConsulTreeModel) super.getModel();
    }
}
