package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.Consul;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
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

    private KeyAndValue selectedKeyAndValue;

    public ConsulTreeModel(JTree tree, Consul consul) {
        this.tree = tree;
        this.consul = consul;

        String treeRootNodeLabel = getTreeRootNodeLabel();
        KVNode unloadedTreeRoot = new KVNode(new RootKeyAndValue().withMessage(treeRootNodeLabel));

        this.delegateModel = new DefaultTreeModel(unloadedTreeRoot);
    }

    private String getTreeRootNodeLabel() {
        return (this.consul != null && this.consul.getConfiguration() != null) ? this.consul.getConfiguration().getHost() : "No data";
    }

    @Override
    public Object getRoot() {
        ConsulTreeLoadingWorker loader = new ConsulTreeLoadingWorker(this.consul);
        if(!this.loaded) {
            loader.setOnDoneListener(treeRoot -> {
                ConsulTreeModel.this.loaded = true;
                DefaultTreeModel delegate = ConsulTreeModel.this.delegateModel;

                String treeRootNodeLabel = getTreeRootNodeLabel();

                treeRoot.setKeyAndValue(new RootKeyAndValue().withMessage(treeRootNodeLabel));

                delegate.setRoot(treeRoot);
            });
            loader.run();
        }

        return getDelegateRoot();
    }

    private KVNode getDelegateRoot() {
        return (KVNode) this.delegateModel.getRoot();
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
        KVNode node = (KVNode) newPath.getLastPathComponent();
        this.selectedKeyAndValue = node.getKeyAndValue();
        this.onValueSelectedListener.onValueSelected(this.selectedKeyAndValue);
    }


    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        this.onValueSelectedListener = onValueSelectedListener;
    }

    public KVNode getNode(KeyAndValue keyAndValue) {
        if (keyAndValue instanceof RootKeyAndValue) {
            return getDelegateRoot();
        }

        String[] components = keyAndValue.getFullyQualifiedKey().split("/");

        KVNode node = (KVNode) getRoot();
        for (String component : components) {
            for (KVNode child : TreeUtils.iterableChildren(node)) {
                KeyAndValue childKV = child.getKeyAndValue();
                if (childKV.getKey().equals(component)) {
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
        KVNode node = getNode(parentKeyAndValue);
        if (node == null) {
            throw new IllegalStateException("Unknown parent node for " + parentKeyAndValue);
        }
        KVNode newNode = new KVNode(newKeyAndValue);
        this.delegateModel.insertNodeInto(newNode, node, newNode.getChildCount());
    }

    public void remove(KeyAndValue value) {
        KVNode node = getNode(value);
        this.delegateModel.removeNodeFromParent(node);
    }

    public void updateNode(KeyAndValue keyAndValue) {
        KVNode node = getNode(keyAndValue);
        node.setKeyAndValue(keyAndValue);
        this.delegateModel.reload(node);
    }


    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        this.delegateModel.addTreeModelListener(treeModelListener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        this.delegateModel.removeTreeModelListener(treeModelListener);
    }

    public Consul getConsul() {
        return consul;
    }

    public KeyAndValue getSelectedKeyAndValue() {
        return selectedKeyAndValue;
    }

    public interface OnValueSelectedListener {
        OnValueSelectedListener INSTANCE = (kv) -> {};

        void onValueSelected(KeyAndValue kv);
    }


}
