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

public class NewEntryAction extends AbstractEntryAction {

    public NewEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "New entry", "Create a new entry", AllIcons.Actions.New);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(fqn);
        if (keyAndValueFromDialog != null) {
            consul.insert(keyAndValueFromDialog.getFullyQualifiedKey(), keyAndValueFromDialog.getValue());
            refreshTree();
        }
    }

    private KeyAndValue getKeyAndValueFromDialog(String fqn) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(new JLabel(fqn));

        JTextField keyTextField = new JTextField();
        panel.add(keyTextField);

        JTextField valueTextField = new JTextField();
        panel.add(valueTextField);

        DialogBuilder builder = new DialogBuilder()
                .title("Delete an entry")
                .centerPanel(panel);
        if(builder.showAndGet()) {
            KeyAndValue keyAndValue = new KeyAndValue(fqn + keyTextField.getText(), valueTextField.getText());
            return keyAndValue;
        }
        return null;
    }

}