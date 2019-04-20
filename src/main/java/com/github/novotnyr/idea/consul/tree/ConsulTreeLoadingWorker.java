package com.github.novotnyr.idea.consul.tree;

import com.ecwid.consul.transport.TransportException;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.ConsulAgentConnectionException;
import com.github.novotnyr.idea.consul.UnauthorizedConsulAccessException;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.SwingWorker;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class ConsulTreeLoadingWorker extends SwingWorker<KVNode, Void> {
    private static final Logger LOG = Logger.getInstance(ConsulTreeLoadingWorker.class);

    private Consul consul;

    private String path = "";

    private OnDoneListener onDoneListener = new OnDoneListener() { /* noop */ };

    public ConsulTreeLoadingWorker(Consul consul) {
        this.consul = consul;
    }

    @Override
    protected KVNode doInBackground() throws Exception {
        LOG.debug("Loading Consul data in background for " + this.consul.getConfiguration().toSimpleString());

        List<GetValue> kvValues = consul.getAllValues();
        Map<String, String> map = Optional.ofNullable(kvValues)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(GetValue::getKey, ConsulTreeLoadingWorker::getValue, throwingMerger(), TreeMap::new));
        return load(map);
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    private static String getValue(GetValue getValue) {
        if (getValue == null || getValue.getValue() == null) {
            return "null";
        }
        return getValue.getDecodedValue();
    }

    @Override
    protected void done() {
        try {
            KVNode node = get();
            this.onDoneListener.onDone(node);
        } catch (InterruptedException | ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof TransportException) {
                TransportException transportException = (TransportException) cause;
                Throwable transportExceptionCause = transportException.getCause();
                if(transportExceptionCause instanceof java.net.ConnectException) {
                    this.onDoneListener.onError(new ConsulAgentConnectionException(cause));
                    return;
                }
            }
            if(cause instanceof OperationException) {
                OperationException operationException = (OperationException) cause;
                if(operationException.getStatusCode() == 401 || operationException.getStatusCode() == 403) {
                    this.onDoneListener.onError(new UnauthorizedConsulAccessException(e));
                    return;
                }
            }
            this.onDoneListener.onError(e.getCause());
        }
    }

    public KVNode load(Map<String, String> map) {
        KVNode root = new KVNode(new RootKeyAndValue());

        map.forEach((key, value) -> {
            String[] components = key.split("/", -1);

            KVNode n = root;
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

                KVNode child = getNodeByValue(n, kv);
                if (child == null) {
                    KVNode newChild = new KVNode(kv);
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
    private KVNode getNodeByValue(TreeNode node, KeyAndValue kv) {
        for (Object childObject : TreeUtils.iterableChildren(node)) {
            KVNode child = (KVNode) childObject;
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
        default void onDone(KVNode treeNode) {

        }

        default void onError(Throwable t) {

        }
    }

}
