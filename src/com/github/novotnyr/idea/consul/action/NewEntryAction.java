package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.JBUI;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class NewEntryAction extends AbstractEntryAction {

    public NewEntryAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "New entry", "Create a new entry", AllIcons.Actions.New);
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
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints cColumn1 = new GridBagConstraints();
        cColumn1.fill = GridBagConstraints.NONE;
        cColumn1.anchor = GridBagConstraints.LINE_START;
        cColumn1.weightx = 0;
        cColumn1.gridx = 0;
        cColumn1.gridy = 0;
        cColumn1.insets = JBUI.insets(5);

        GridBagConstraints cColumns2 = new GridBagConstraints();
        cColumns2.fill = GridBagConstraints.HORIZONTAL;
        cColumn1.anchor = GridBagConstraints.LINE_START;
        cColumns2.weightx = 1;
        cColumns2.gridx = 1;
        cColumns2.gridy = 0;
        cColumns2.insets = JBUI.insets(5);

        // row 1
        panel.add(new JLabel("Parent:"), cColumn1);
        panel.add(new JLabel(fqn), cColumns2);

        // row 2
        cColumn1.gridy = cColumns2.gridy = 1;

        panel.add(new JLabel("Key:"), cColumn1);

        JTextField keyTextField = new JTextField();
        panel.add(keyTextField, cColumns2);

        // row 3
        cColumn1.gridy = cColumns2.gridy = 2;
        cColumns2.fill = GridBagConstraints.BOTH;
        cColumns2.weighty = 1;

        panel.add(new JLabel("Value:"), cColumn1);

        JTextArea valueTextArea = new JTextArea();
        JBScrollPane valueScrollPane = new JBScrollPane(valueTextArea);
        valueScrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(valueScrollPane, cColumns2);

        panel.setPreferredSize(new Dimension(-1, 200));

        DialogBuilder builder = new DialogBuilder()
                .title("New Entry")
                .centerPanel(panel);
        if(builder.showAndGet()) {
            KeyAndValue keyAndValue = new KeyAndValue(fqn + keyTextField.getText(), valueTextArea.getText());
            return keyAndValue;
        }
        return null;
    }

}