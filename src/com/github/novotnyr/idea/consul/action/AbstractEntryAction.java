package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.Topics;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.messages.MessageBus;

import javax.swing.Icon;

public abstract class AbstractEntryAction extends AnAction {
    protected Consul consul;

    protected MessageBus messageBus;

    protected KeyAndValue selectedKeyAndValue;

    private boolean enabled = false;

    protected ConsulTreeModel treeModel;

    public AbstractEntryAction(Consul consul, MessageBus messageBus, String text, String description, Icon icon) {
        this(consul, messageBus, text, description, icon, false);
    }
    public AbstractEntryAction(Consul consul, MessageBus messageBus, String text, String description, Icon icon, boolean enabled) {
        super(text, description, icon);
        this.consul = consul;
        this.messageBus = messageBus;
        this.enabled = true;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        String fqn = this.selectedKeyAndValue.getFullyQualifiedKey();
        onActionPerformed(fqn, event);
    }

    protected abstract void onActionPerformed(String fqn, AnActionEvent event);

    protected void refreshTree() {
        messageBus.syncPublisher(Topics.RefreshTree.REFRESH_TREE_TOPIC).refreshTree();
    }


    @Override
    public void update(AnActionEvent event) {
        if ("ConsulExplorer".equals(event.getPlace())) {
            KeyAndValue selectedKeyAndValue = (KeyAndValue) event.getDataContext().getData("selectedKeyAndValue");
            this.enabled = selectedKeyAndValue != null && isEnabledForKeyAndValue(selectedKeyAndValue);;
            this.selectedKeyAndValue = selectedKeyAndValue;
        }
        event.getPresentation().setEnabled(this.enabled);
    }

    protected boolean isEnabledForKeyAndValue(KeyAndValue keyAndValue) {
        return true;
    }

    public void setTreeModel(ConsulTreeModel treeModel) {
        this.treeModel = treeModel;
    }
}
