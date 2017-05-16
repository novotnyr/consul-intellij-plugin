package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.KeyAndValueEditorPanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.util.messages.MessageBus;

public class NewEntryAction extends AbstractEntryAction {

    public NewEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "New entry", "Create a new entry", AllIcons.ToolbarDecorator.Add);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(fqn);
        if (keyAndValueFromDialog != null) {
            this.treeModel.addNode(this.selectedKeyAndValue, keyAndValueFromDialog);
            consul.insert(keyAndValueFromDialog.getFullyQualifiedKey(), keyAndValueFromDialog.getValue());
        }
    }

    private KeyAndValue getKeyAndValueFromDialog(String fqn) {
        KeyAndValueEditorPanel keyAndValuePanel = new KeyAndValueEditorPanel(fqn);

        DialogBuilder builder = new DialogBuilder()
                .title("Create a new entry")
                .centerPanel(keyAndValuePanel);
        builder.setPreferredFocusComponent(keyAndValuePanel.getKeyTextField());
        if(builder.showAndGet()) {
            return keyAndValuePanel.getKeyAndValue();
        }
        return null;
    }

    @Override
    protected boolean isEnabledForKeyAndValue(KeyAndValue keyAndValue) {
        return keyAndValue.isContainer();
    }

}