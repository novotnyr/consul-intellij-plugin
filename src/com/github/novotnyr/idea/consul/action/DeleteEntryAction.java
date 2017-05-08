package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.util.messages.MessageBus;

import javax.swing.JLabel;

public class DeleteEntryAction extends AbstractEntryAction {

    public DeleteEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "Delete", "Delete an entry", AllIcons.Actions.Delete);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        if(confirmInDialog(fqn)) {
            consul.delete(fqn);
            refreshTree();
        }
    }

    private boolean confirmInDialog(String fqn) {
        JLabel keyLabel = new JLabel(fqn);
        DialogBuilder builder = new DialogBuilder()
                .title("Delete an entry")
                .centerPanel(keyLabel);
        return builder.showAndGet();
    }
}
