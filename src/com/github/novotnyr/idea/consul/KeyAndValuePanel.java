package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.FolderContentsTablePanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class KeyAndValuePanel extends JPanel {
    private MessageBus messageBus;

    private JLabel fqnKeyLabel = new JLabel("N/A");

    private JTextArea valueTextArea = new JTextArea(10, 15);

    private JButton updateButton = new JButton("Update");

    private KeyAndValue keyAndValue;

    private FolderContentsTablePanel folderContentsTablePanel;

    private ConsulTreeModel consulTree;

    private final JPanel middlePanel;

    private Mode viewMode;

    public KeyAndValuePanel(@NotNull MessageBus messageBus, @NotNull ConsulTreeModel consulTree) {
        super();
        this.consulTree = consulTree;
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

        this.middlePanel = new JPanel(new CardLayout());
        this.middlePanel.add(getValuePane(), Mode.ENTRY.name());
        this.middlePanel.add(getFolderContentPane(), Mode.FOLDER.name());

        add(this.middlePanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(20, 200));
    }

    private JBScrollPane getValuePane() {
        this.valueTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return new JBScrollPane(this.valueTextArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private JComponent getFolderContentPane() {
        this.folderContentsTablePanel = new FolderContentsTablePanel(consulTree, this.keyAndValue);
        return this.folderContentsTablePanel;
    }

    private void onUpdateButtonClick(ActionEvent event) {
        this.messageBus
                .syncPublisher(Topics.KeyValueChanged.KEY_VALUE_CHANGED)
                .keyValueChanged(this.fqnKeyLabel.getText(), this.valueTextArea.getText());
    }

    public void setKeyAndValue(KeyAndValue keyAndValue) {
        this.keyAndValue = keyAndValue;
        this.fqnKeyLabel.setText(keyAndValue.getFullyQualifiedKey());

        CardLayout cardLayout = (CardLayout) this.middlePanel.getLayout();
        if(keyAndValue.isContainer()) {
            this.folderContentsTablePanel.refresh(this.consulTree, this.keyAndValue);
            cardLayout.show(this.middlePanel, Mode.FOLDER.name());
            this.updateButton.setVisible(false);
        } else {
            this.valueTextArea.setText(keyAndValue.getValue());
            cardLayout.show(this.middlePanel, Mode.ENTRY.name());
            this.updateButton.setVisible(true);
        }
    }

    public enum Mode {
        FOLDER,
        ENTRY;
    }
}
