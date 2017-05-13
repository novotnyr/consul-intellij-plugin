package com.github.novotnyr.idea.consul.tree;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeUtils {
    @SuppressWarnings("unchecked")
    public static <N extends TreeNode> Iterable<N> iterableChildren(N node) {
        List childrenList = Collections.list(node.children());
        List<N> newChildrenList = new ArrayList<>(childrenList.size());
        for (Object c : childrenList) {
            newChildrenList.add((N) c);
        }
        return newChildrenList;
    }

    public static AnActionEvent getEventForSelectedKeyAndValue(KeyAndValue keyAndValue) {
        DataContext dataContext = SimpleDataContext.getSimpleContext("selectedKeyAndValue", keyAndValue);
        AnActionEvent event = AnActionEvent.createFromDataContext("ConsulExplorer", null, dataContext);

        return event;
    }
}
