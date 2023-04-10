package com.github.novotnyr.idea.consul.tree;

import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public static void expandConsulTree(final ConsulTree tree, TreePath treePath) {
        List<KVNode> nodes = new ArrayList<>();
        Comparator<KVNode> comparator = Comparator.comparing(n -> n.getKeyAndValue().getFullyQualifiedKey());
        KVNode root = (KVNode) tree.getModel().getRoot();
        nodes.add(root);
        collectRefreshedPath(tree, treePath, root, comparator, nodes);
        TreePath restoredTreePath = new TreePath(nodes.toArray());
        tree.expandPath(restoredTreePath);
    }

    /**
     * Recollects tree path after tree refresh
     * @param tree tree that was refreshed
     * @param oldTreePath original tree path before refresh
     * @param node tree parameter passed between recursive invications
     * @param comparator function that compares two nodes after refreshes
     * @param newPathAccumulator accumulates new refreshed tree path
     * @param <N> type of node
     */
    @SuppressWarnings("unchecked")
    private static <N> void collectRefreshedPath(final Tree tree, TreePath oldTreePath, N node, final Comparator<N> comparator, List<N> newPathAccumulator) {
        if (oldTreePath == null || oldTreePath.getPathCount() == 0) {
            return;
        }
        N firstComponent = (N) oldTreePath.getPathComponent(0);
        for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
            N child = (N) tree.getModel().getChild(node, i);
            if (comparator.compare(firstComponent, child) == 0) {
                newPathAccumulator.add(child);
                TreePath shortenedOldTreePath = removeHead(oldTreePath);
                collectRefreshedPath(tree, shortenedOldTreePath, child, comparator, newPathAccumulator);
            }
        }
    }

    @Nullable
    public static TreePath removeHead(TreePath treePath) {
        TreePath newPath = null;
        // start with 1, skip first (0th) path component
        for (int i = 1; i < treePath.getPathCount(); i++) {
            Object pathComponent = treePath.getPathComponent(i);
            if (i == 1) {
                newPath = new TreePath(pathComponent);
            } else {
                newPath = newPath.pathByAddingChild(pathComponent);
            }
        }

        return newPath;
    }
}
