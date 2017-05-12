package com.github.novotnyr.idea.consul.tree;

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
}
