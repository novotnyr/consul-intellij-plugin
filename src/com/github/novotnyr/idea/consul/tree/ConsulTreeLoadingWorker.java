package com.github.novotnyr.idea.consul.tree;

import com.ecwid.consul.v1.kv.model.GetValue;
import com.github.novotnyr.idea.consul.Consul;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ConsulTreeLoadingWorker extends SwingWorker<TreeNode, Void> {
    private static final Logger LOG = Logger.getInstance(ConsulTreeLoadingWorker.class);

    private Consul consul;

    private String path = "";

    private OnDoneListener onDoneListener = cache -> { /* no-op */};

    // shared between threads!
    // created on EDT, modified on background
    private DefaultMutableTreeNode treeRootNode;

    public ConsulTreeLoadingWorker(Consul consul, DefaultMutableTreeNode treeRootNode) {
        this.consul = consul;
        this.treeRootNode = treeRootNode;
    }

    @Override
    protected TreeNode doInBackground() throws Exception {
        LOG.debug("Loading Consul data in background for " + this.consul.getConfiguration().toSimpleString());

        List<GetValue> kvValues = consul.getAllValues();
        Map<String, String> map = Optional.ofNullable(kvValues)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(GetValue::getKey, this::getValue));
        return load(map, treeRootNode);
    }

    private String getValue(GetValue getValue) {
        if (getValue == null || getValue.getValue() == null) {
            return "null";
        }
        return getValue.getDecodedValue();
    }

    @Override
    protected void done() {
        try {
            TreeNode node = get();
            this.onDoneListener.onDone(node);
        } catch (InterruptedException | ExecutionException e) {
            ((RootKeyAndValue) treeRootNode.getUserObject()).setMessage("No data!");
            this.onDoneListener.onDone(treeRootNode);
        }
    }

    public DefaultMutableTreeNode load(Map<String, String> map, DefaultMutableTreeNode root) {
        map.forEach((key, value) -> {
            String[] components = key.split("/", -1);

            DefaultMutableTreeNode n = root;
            StringBuilder fullPath = new StringBuilder();
            for (int i = 0; i < components.length; i++) {
                String component = components[i];
                appendToFullPath(fullPath, component);
                KeyAndValue kv;
                if (i < components.length - 1) {
                    kv = new KeyAndValue(fullPath.toString());
                } else {
                    if(key.endsWith("/") && component.isEmpty()) {
                        break;
                    }
                    kv = new KeyAndValue(fullPath.toString(), value);
                }

                DefaultMutableTreeNode child = getNodeByValue(n, kv);
                if (child == null) {
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(kv);
                    n.insert(newChild, n.getChildCount());
                    n = newChild;
                } else {
                    n = child;
                }
            }
            n.setAllowsChildren(key.endsWith("/"));
        });
        return root;
    }

    private void appendToFullPath(StringBuilder fullPath, String component) {
        if(fullPath.length() == 0) {
            fullPath.append(component);
        } else {
            fullPath.append("/").append(component);
        }

    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getNodeByValue(TreeNode node, KeyAndValue kv) {
        for(Object childObject : Collections.list(node.children())) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) childObject;
            if (child.getUserObject().equals(kv)) {
                return child;
            }
        }
        return null;
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        this.onDoneListener = onDoneListener;
    }

    public interface OnDoneListener {
        void onDone(TreeNode treeNode);
    }

}
