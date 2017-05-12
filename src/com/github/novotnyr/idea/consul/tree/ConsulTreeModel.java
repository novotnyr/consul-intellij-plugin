package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.Consul;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ConsulTreeModel implements TreeWillExpandListener, TreeSelectionListener, TreeModel {
    private JTree tree;

    private Consul consul;

    private boolean loaded = false;

    private OnValueSelectedListener onValueSelectedListener = OnValueSelectedListener.INSTANCE;

    private DefaultTreeModel delegateModel;

    public ConsulTreeModel(JTree tree, Consul consul) {
        this.tree = tree;
        this.consul = consul;

        String treeRootNodeLabel = (consul != null && consul.getConfiguration() != null) ? consul.getConfiguration().getHost() : "No data";
        DefaultMutableTreeNode unloadedTreeRoot = new DefaultMutableTreeNode(new RootKeyAndValue().withMessage(treeRootNodeLabel));

        this.delegateModel = new DefaultTreeModel(unloadedTreeRoot);
    }

    @Override
    public Object getRoot() {
        ConsulTreeLoadingWorker loader = new ConsulTreeLoadingWorker(consul, getDelegateRoot());
        if(!loaded) {
            loader.setOnDoneListener(treeRoot -> {
                ConsulTreeModel.this.loaded = true;
            });
            loader.run();
        }

        return getDelegateRoot();
    }

    private DefaultMutableTreeNode getDelegateRoot() {
        return (DefaultMutableTreeNode) this.delegateModel.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return this.delegateModel.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        return this.delegateModel.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object nodeObject) {
        return this.delegateModel.isLeaf(nodeObject);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        this.delegateModel.valueForPathChanged(path, newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        TreeNode parentNode = (TreeNode) parent;
        return parentNode.getIndex((TreeNode) child);
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {

    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath newPath = e.getNewLeadSelectionPath();
        if(newPath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) newPath.getLastPathComponent();
        this.onValueSelectedListener.onValueSelected((KeyAndValue) node.getUserObject());
    }


    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        this.onValueSelectedListener = onValueSelectedListener;
    }

    public DefaultMutableTreeNode getNode(KeyAndValue keyAndValue) {
        if (keyAndValue instanceof RootKeyAndValue) {
            return getDelegateRoot();
        }

        String[] components = keyAndValue.getFullyQualifiedKey().split("/");

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getRoot();
        for (int i = 0; i < components.length; i++) {
            for (DefaultMutableTreeNode child : TreeUtils.iterableChildren(node)) {
                KeyAndValue childKV = (KeyAndValue) child.getUserObject();
                if (childKV.getKey().equals(components[i])) {
                    node = child;
                    break;
                }
            }
        }
        if(node == getRoot()) {
            return null;
        }
        return node;
    }

    public void addNode(KeyAndValue parentKeyAndValue, KeyAndValue newKeyAndValue) {
        DefaultMutableTreeNode node = getNode(parentKeyAndValue);
        if (node == null) {
            throw new IllegalStateException("Unknown parent node for " + parentKeyAndValue);
        }
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newKeyAndValue);
        this.delegateModel.insertNodeInto(newNode, node, newNode.getChildCount());
    }

    public void remove(KeyAndValue value) {
        DefaultMutableTreeNode node = getNode(value);
        this.delegateModel.removeNodeFromParent(node);
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        this.delegateModel.addTreeModelListener(treeModelListener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        this.delegateModel.removeTreeModelListener(treeModelListener);
    }

    public interface OnValueSelectedListener {
        public static final OnValueSelectedListener INSTANCE = (kv) -> {};

        void onValueSelected(KeyAndValue kv);
    }


}
