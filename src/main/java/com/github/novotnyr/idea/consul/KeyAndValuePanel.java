package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.BottomToolWindowPanel;
import com.github.novotnyr.idea.consul.ui.FolderContentsTablePanel;
import com.github.novotnyr.idea.consul.ui.UnsynchronizedChangesLabelAction;
import com.github.novotnyr.idea.consul.ui.event.IgnorableDocumentAdapter;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class KeyAndValuePanel extends JPanel {
    private MessageBus messageBus;

    private JLabel fqnKeyLabel = new JLabel("N/A");

    private JTextArea valueTextArea = new JTextArea();

    private KeyAndValue keyAndValue;

    private FolderContentsTablePanel folderContentsTablePanel;

    private ConsulTreeModel consulTree;

    private final JPanel middlePanel;

    private SubmitChangesAction submitChangesAction;

    private UnsynchronizedChangesLabelAction unsynchronizedChangesLabel;

    private IgnorableDocumentAdapter valueTextAreaDocumentListener;

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

        MessageBusConnection messageBusConnection = this.messageBus.connect();
        messageBusConnection.subscribe(Topics.KeyValueChanged.KEY_VALUE_CHANGED,
                keyAndValue -> {
                    this.unsynchronizedChangesLabel.hideText();
                    KeyAndValuePanel.this.submitChangesAction.setEnabled(false);
                }
        );
        messageBusConnection.subscribe(Topics.ConsulTreeSelectionChanged.CONSUL_TREE_SELECTION_CHANGED,
                keyAndValue -> {
                    this.unsynchronizedChangesLabel.hideText();
                    KeyAndValuePanel.this.submitChangesAction.setEnabled(false);
                }
        );
    }

    private JComponent getValuePane() {
        this.valueTextAreaDocumentListener = new IgnorableDocumentAdapter() {
            @Override
            protected void doTextChanged(DocumentEvent documentEvent) {
                KeyAndValuePanel.this.submitChangesAction.setEnabled(true);
                KeyAndValuePanel.this.unsynchronizedChangesLabel.showText();
            }
        };
        this.valueTextArea.getDocument().addDocumentListener(this.valueTextAreaDocumentListener);
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
        group.add(this.unsynchronizedChangesLabel = new UnsynchronizedChangesLabelAction());

        return group;
    }


    private JComponent getFolderContentPane() {
        this.folderContentsTablePanel = new FolderContentsTablePanel(this.consulTree, this.keyAndValue);
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
            this.valueTextAreaDocumentListener.disableEventHandling();
            this.valueTextArea.setText(keyAndValue.getValue());
            this.valueTextAreaDocumentListener.enableEventHandling();
            cardLayout.show(this.middlePanel, Mode.ENTRY.name());
        }
    }

    public void setTreeModel(ConsulTreeModel consulTree) {
        this.consulTree = consulTree;
    }

    public class SubmitChangesAction extends AnAction {
        private boolean enabled;

        public SubmitChangesAction() {
            super("Submit changes", "Submit a new value", AllIcons.Actions.Menu_saveall);
        }

        @Override
        public void actionPerformed(AnActionEvent event) {
            KeyAndValuePanel.this.messageBus
                    .syncPublisher(Topics.KeyValueChanged.KEY_VALUE_CHANGED)
                    .keyValueChanged(new KeyAndValue(KeyAndValuePanel.this.fqnKeyLabel.getText(), KeyAndValuePanel.this.valueTextArea.getText()));
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setEnabled(this.enabled);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public enum Mode {
        FOLDER,
        ENTRY;
    }
}
