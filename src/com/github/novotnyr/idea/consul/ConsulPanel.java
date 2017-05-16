package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.action.NewEntryAction;
import com.github.novotnyr.idea.consul.action.NewFolderAction;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;

public class ConsulPanel extends JPanel {
    private MessageBus messageBus;

    private Tree tree;

    private ConsulTreeModel treeModel;

    private Consul consul;

    private KeyAndValuePanel keyAndValuePanel;

    private ConsulTreeModel.OnValueSelectedListener treeValueSelectedListener = kv -> { };

    public ConsulPanel(@NotNull Consul consul, @NotNull MessageBus messageBus) {
        this.consul = consul;
        this.messageBus = messageBus;

        setLayout(new BorderLayout());

        this.tree = new Tree();
        initializeTreeModel();
        TreeUtil.installActions(tree);
        installPopupHandler(tree);

        this.keyAndValuePanel = new KeyAndValuePanel(this.messageBus, treeModel);


        JBSplitter splitter = new JBSplitter(true, 0.8f, 0.1f, 0.9f);
        splitter.setFirstComponent(new JBScrollPane(this.tree));
        splitter.setSecondComponent(this.keyAndValuePanel);
        splitter.setHonorComponentsMinimumSize(true);

        add(splitter, BorderLayout.CENTER);
    }

    private void installPopupHandler(Tree tree) {
        tree.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(final Component comp, final int x, final int y) {
                popupInvoked(comp, x, y);
            }
        });
    }

    private void popupInvoked(final Component comp, final int x, final int y) {
        KeyAndValue keyAndValue = null;
        final TreePath path = tree.getSelectionPath();
        if (path != null) {
            KVNode node = (KVNode) path.getLastPathComponent();
            if (node != null) {
                keyAndValue = node.getKeyAndValue();
            }
        }
        DefaultActionGroup group = new DefaultActionGroup();
        NewEntryAction newEntryAction = new NewEntryAction(this.consul, this.messageBus);
        newEntryAction.setSelectedKeyAndValue(keyAndValue);
        newEntryAction.setTreeModel(this.treeModel);

        group.add(newEntryAction);

        NewFolderAction newFolderAction = new NewFolderAction(this.consul, this.messageBus);
        newFolderAction.setSelectedKeyAndValue(keyAndValue);
        newFolderAction.setTreeModel(this.treeModel);

        group.add(newFolderAction);

        ActionPopupMenu popupMenu = ActionManager.getInstance()
                .createActionPopupMenu("ConsulTreePopup", group);
        popupMenu.getComponent().show(comp, x, y);
    }

    private void initializeTreeModel() {
        treeModel = new ConsulTreeModel(tree, this.consul);
        treeModel.setOnValueSelectedListener(this::treeValueSelected);
        tree.setModel(treeModel);
        tree.addTreeWillExpandListener(this.treeModel);
        tree.addTreeSelectionListener(this.treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void treeValueSelected(KeyAndValue kv) {
        this.keyAndValuePanel.setKeyAndValue(kv);
        this.treeValueSelectedListener.onValueSelected(kv);
    }

    public void setTreeValueSelectedListener(ConsulTreeModel.OnValueSelectedListener treeValueSelectedListener) {
        this.treeValueSelectedListener = treeValueSelectedListener;
    }

    public void refresh() {
        initializeTreeModel();
    }

    public ConsulTreeModel getTreeModel() {
        return treeModel;
    }
}
