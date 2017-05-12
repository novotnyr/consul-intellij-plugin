package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.KeyAndValueEditorPanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.util.messages.MessageBus;

public class UpdateEntryAction extends AbstractEntryAction {
    public UpdateEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "Edit entry", "Edit changes", AllIcons.ToolbarDecorator.Edit);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(fqn);
        if (keyAndValueFromDialog != null) {
            update(keyAndValueFromDialog.getFullyQualifiedKey(), keyAndValueFromDialog.getValue());
        }
    }

    public void update(String fqnKey, String value) {
        consul.update(fqnKey, value);
        refreshTree();
    }


    private KeyAndValue getKeyAndValueFromDialog(String fqn) {
        KeyAndValueEditorPanel keyAndValuePanel = new KeyAndValueEditorPanel(this.selectedKeyAndValue, fqn);

        DialogBuilder builder = new DialogBuilder()
                .title("Update an entry")
                .centerPanel(keyAndValuePanel);
        builder.setPreferredFocusComponent(keyAndValuePanel.getKeyTextField());
        if(builder.showAndGet()) {
            return keyAndValuePanel.getKeyAndValue();
        }
        return null;
    }

    @Override
    protected boolean isEnabledForKeyAndValue(KeyAndValue keyAndValue) {
        return ! keyAndValue.isContainer();
    }
}
