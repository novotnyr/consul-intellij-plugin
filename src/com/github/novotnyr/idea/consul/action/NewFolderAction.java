package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.util.messages.MessageBus;

import javax.swing.JTextField;

public class NewFolderAction extends AbstractEntryAction {
    public NewFolderAction(Consul consulClient, MessageBus messageBus) {
        super(consulClient, messageBus, "New Folder", "Create a new folder", AllIcons.Actions.NewFolder);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        JTextField folderNameTextField = new JTextField();
        DialogBuilder builder = new DialogBuilder()
                .title("New Folder name")
                .centerPanel(folderNameTextField);
        builder.setPreferredFocusComponent(folderNameTextField);
        if (builder.showAndGet()) {
            String fullyQualifiedPath = fqn + folderNameTextField.getText();
            consul.mkdir(fullyQualifiedPath);
            refreshTree();
        }

    }
}