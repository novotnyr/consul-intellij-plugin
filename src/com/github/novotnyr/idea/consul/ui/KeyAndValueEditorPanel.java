package com.github.novotnyr.idea.consul.ui;

import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class KeyAndValueEditorPanel extends JPanel {
    private KeyAndValue keyAndValue;

    private String parentFullyQualifiedName;

    private JLabel parentFqnLabel;

    private JTextArea valueTextArea;

    private JTextField keyTextField;

    public KeyAndValueEditorPanel(String parentFullyQualifiedName) {
        this.parentFullyQualifiedName = parentFullyQualifiedName;
        initComponents();
        this.parentFqnLabel.setText(parentFullyQualifiedName);
        this.keyTextField.setEnabled(true);
    }

    public KeyAndValueEditorPanel(KeyAndValue keyAndValue) {
        initComponents();
        this.keyAndValue = keyAndValue;
        this.parentFullyQualifiedName = keyAndValue.getParentFullyQualifiedKey();
        bindFromModel();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

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
        add(new JLabel("Parent:"), cColumn1);
        add(this.parentFqnLabel = new JLabel(parentFullyQualifiedName), cColumns2);

        // row 2
        cColumn1.gridy = cColumns2.gridy = 1;

        add(new JLabel("Key:"), cColumn1);

        add(this.keyTextField = new JTextField(), cColumns2);
        this.keyTextField.setEnabled(false);

        // row 3
        cColumn1.gridy = cColumns2.gridy = 2;
        cColumns2.fill = GridBagConstraints.BOTH;
        cColumns2.weighty = 1;

        add(new JLabel("Value:"), cColumn1);

        JBScrollPane valueScrollPane = new JBScrollPane(this.valueTextArea = new JTextArea());
        this.valueTextArea.setLineWrap(true);
        this.valueTextArea.setWrapStyleWord(true);
        valueScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(valueScrollPane, cColumns2);
    }

    private void bindFromModel() {
        if (this.keyAndValue == null) {
            return;
        }

        this.parentFqnLabel.setText(this.parentFullyQualifiedName);
        this.keyTextField.setText(this.keyAndValue.getKey());
        this.valueTextArea.setText(this.keyAndValue.getValue());
    }

    public KeyAndValue getKeyAndValue() {
        return new KeyAndValue(this.parentFullyQualifiedName
                + this.keyTextField.getText(), this.valueTextArea.getText());
    }

    public JTextField getKeyTextField() {
        return keyTextField;
    }
}
