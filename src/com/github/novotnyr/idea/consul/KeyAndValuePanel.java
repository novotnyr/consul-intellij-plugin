package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.BottomToolWindowPanel;
import com.github.novotnyr.idea.consul.ui.FolderContentsTablePanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class KeyAndValuePanel extends JPanel {
    private MessageBus messageBus;

    private JLabel fqnKeyLabel = new JLabel("N/A");

    private JTextArea valueTextArea = new JTextArea(10, 15);

    private KeyAndValue keyAndValue;

    private FolderContentsTablePanel folderContentsTablePanel;

    private ConsulTreeModel consulTree;

    private final JPanel middlePanel;

    private SubmitChangesAction submitChangesAction;

    private Mode viewMode;

    public KeyAndValuePanel(@NotNull MessageBus messageBus, @NotNull ConsulTreeModel consulTree) {
        super();
        this.consulTree = consulTree;
        setLayout(new BorderLayout());

        this.messageBus = messageBus;

        this.fqnKeyLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
        headerPanel.add(this.fqnKeyLabel);

        add(headerPanel, BorderLayout.PAGE_START);

        this.middlePanel = new JPanel(new CardLayout());
        this.middlePanel.add(getValuePane(), Mode.ENTRY.name());
        this.middlePanel.add(getFolderContentPane(), Mode.FOLDER.name());

        add(this.middlePanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(20, 200));
    }

    private JComponent getValuePane() {
        this.valueTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JBScrollPane scrollPane = new JBScrollPane(this.valueTextArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return wrapWithToolbar(scrollPane);
    }

    @NotNull
    private SimpleToolWindowPanel wrapWithToolbar(JBScrollPane scrollPane) {
        BottomToolWindowPanel panel = new BottomToolWindowPanel();
        panel.setContent(scrollPane);

        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("consulKeyAndValueToolbar", createToolbarActionGroup(), true);
        actionToolBar.setTargetComponent(scrollPane);
        panel.setToolbar(actionToolBar.getComponent());

        return panel;
    }

    private DefaultActionGroup createToolbarActionGroup() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(this.submitChangesAction = new SubmitChangesAction());

        return group;
    }


    private JComponent getFolderContentPane() {
        this.folderContentsTablePanel = new FolderContentsTablePanel(consulTree, this.keyAndValue);
        return this.folderContentsTablePanel;
    }

    public void setKeyAndValue(KeyAndValue keyAndValue) {
        this.keyAndValue = keyAndValue;
        this.fqnKeyLabel.setText(keyAndValue.getFullyQualifiedKey());

        CardLayout cardLayout = (CardLayout) this.middlePanel.getLayout();
        if(keyAndValue.isContainer()) {
            this.folderContentsTablePanel.refresh(this.consulTree, this.keyAndValue);
            cardLayout.show(this.middlePanel, Mode.FOLDER.name());
        } else {
            this.valueTextArea.setText(keyAndValue.getValue());
            cardLayout.show(this.middlePanel, Mode.ENTRY.name());
        }
    }

    public class SubmitChangesAction extends AnAction {

        public SubmitChangesAction() {
            super("Submit changes", "Submit a new value", AllIcons.Actions.Upload);
        }

        @Override
        public void actionPerformed(AnActionEvent event) {
            KeyAndValuePanel.this.messageBus
                    .syncPublisher(Topics.KeyValueChanged.KEY_VALUE_CHANGED)
                    .keyValueChanged(new KeyAndValue(KeyAndValuePanel.this.fqnKeyLabel.getText(), KeyAndValuePanel.this.valueTextArea.getText()));
        }
    }

    public enum Mode {
        FOLDER,
        ENTRY;
    }
}
