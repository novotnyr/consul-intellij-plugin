package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;

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

        this.keyAndValuePanel = new KeyAndValuePanel(this.messageBus, treeModel);


        JBSplitter splitter = new JBSplitter(true, 0.8f, 0.1f, 0.9f);
        splitter.setFirstComponent(new JBScrollPane(this.tree));
        splitter.setSecondComponent(this.keyAndValuePanel);
        splitter.setHonorComponentsMinimumSize(true);

        add(splitter, BorderLayout.CENTER);
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
