package com.github.novotnyr.idea.consul;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class KeyAndValuePanel extends JPanel {
    private MessageBus messageBus;

    private JLabel fqnKeyLabel = new JLabel("N/A");

    private JTextArea valueTextArea = new JTextArea(10, 15);

    private JButton updateButton = new JButton("Update");

    public KeyAndValuePanel(@NotNull MessageBus messageBus) {
        super();
        setLayout(new BorderLayout());

        this.messageBus = messageBus;

        this.fqnKeyLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.updateButton.addActionListener(this::onUpdateButtonClick);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
        headerPanel.add(this.fqnKeyLabel);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(this.updateButton);

        add(headerPanel, BorderLayout.PAGE_START);


        this.valueTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JBScrollPane(this.valueTextArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        setMinimumSize(new Dimension(20, 200));
    }

    private void onUpdateButtonClick(ActionEvent event) {
        this.messageBus
                .syncPublisher(Topics.KeyValueChanged.KEY_VALUE_CHANGED)
                .keyValueChanged(this.fqnKeyLabel.getText(), this.valueTextArea.getText());
    }

    public void setKey(String key) {
        this.fqnKeyLabel.setText(key);
    }

    public void setValue(String value) {
        this.valueTextArea.setText(value);
    }
}
