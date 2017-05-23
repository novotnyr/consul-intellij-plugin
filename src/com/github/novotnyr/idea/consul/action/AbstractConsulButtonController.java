package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.AnActionButtonUpdater;

import java.awt.Component;
import java.util.Optional;

public abstract class AbstractConsulButtonController implements AnActionButtonRunnable, AnActionButtonUpdater {
    private ConsulTree consulTree;

    private Consul consul;

    public AbstractConsulButtonController(Consul consul) {
        this.consul = consul;
    }

    @Override
    public boolean isEnabled(AnActionEvent event) {
        Optional<ConsulTree> maybeConsulTree = getConsulTree(event);
        if(maybeConsulTree.isPresent()) {
            this.consulTree = maybeConsulTree.get();
            return consulTree.getSelectionModel().getSelectionCount() > 0;
        } else {
            return false;
        }
    }

    public boolean isEnabled(ConsulTree tree) {
        this.consulTree = tree;
        return tree.getSelectionModel().getSelectionCount() > 0;
    }

    public ConsulTree getConsulTree() {
        return this.consulTree;
    }

    public String getFqn() {
        return getSelectedKeyAndValue().getFullyQualifiedKey();
    }

    protected KeyAndValue getSelectedKeyAndValue() {
        return this.consulTree.getConsulTreeModel().getSelectedKeyAndValue();
    }

    public Consul getConsul() {
        return this.consul;
    }

    public ConsulTreeModel getTreeModel() {
        return this.consulTree.getConsulTreeModel();
    }

    private Optional<ConsulTree> getConsulTree(AnActionEvent event) {
        Component component = event.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if(component instanceof ConsulTree) {
            return Optional.of((ConsulTree) component);
        }
        return Optional.empty();
    }
}
