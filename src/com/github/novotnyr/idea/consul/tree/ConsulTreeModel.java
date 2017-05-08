package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.Consul;
import com.intellij.util.ui.tree.AbstractTreeModel;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ConsulTreeModel extends AbstractTreeModel implements TreeWillExpandListener, TreeSelectionListener {
    private JTree tree;

    private Consul consul;

    private boolean loaded = false;

    private OnValueSelectedListener onValueSelectedListener = OnValueSelectedListener.INSTANCE;

    private DefaultMutableTreeNode treeRootNode;

    public ConsulTreeModel(JTree tree, Consul consul) {
        this.tree = tree;
        this.consul = consul;

        String treeRootNodeLabel = (consul != null && consul.getConfiguration() != null) ? consul.getConfiguration().getHost() : "No data";
        this.treeRootNode = new DefaultMutableTreeNode(new RootKeyAndValue().withMessage(treeRootNodeLabel));
    }

    @Override
    public Object getRoot() {
        ConsulTreeLoadingWorker loader = new ConsulTreeLoadingWorker(consul, treeRootNode);
        if(!loaded) {
            loader.setOnDoneListener(treeRoot -> {
                ConsulTreeModel.this.loaded = true;
            });
            loader.run();
        }

        return treeRootNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        TreeNode parentNode = (TreeNode) parent;
        return parentNode.getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        TreeNode parentNode = (TreeNode) parent;
        return ((TreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object nodeObject) {
        TreeNode node = (TreeNode) nodeObject;
        return node.isLeaf() && ! node.getAllowsChildren();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
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

    public interface OnValueSelectedListener {
        public static final OnValueSelectedListener INSTANCE = (kv) -> {};

        void onValueSelected(KeyAndValue kv);
    }
}
