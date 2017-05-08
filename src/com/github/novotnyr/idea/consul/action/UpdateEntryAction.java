package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.util.messages.MessageBus;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UpdateEntryAction extends AbstractEntryAction {
    public UpdateEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "Edit entry", "Edit changes", AllIcons.Actions.Edit);
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(new JLabel(fqn));

        JTextField valueTextField = new JTextField(this.selectedKeyAndValue.getValue());
        panel.add(valueTextField);

        DialogBuilder builder = new DialogBuilder()
                .title("Update an entry")
                .centerPanel(panel);
        if(builder.showAndGet()) {
            KeyAndValue keyAndValue = new KeyAndValue(this.selectedKeyAndValue.getFullyQualifiedKey(), valueTextField.getText());
            return keyAndValue;
        }
        return null;
    }
}
