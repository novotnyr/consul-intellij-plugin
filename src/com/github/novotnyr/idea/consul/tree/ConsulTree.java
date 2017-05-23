package com.github.novotnyr.idea.consul.tree;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.Nullable;

public class ConsulTree extends Tree implements DataProvider {
    public ConsulTreeModel getConsulTreeModel() {
        return (ConsulTreeModel) super.getModel();
    }

    @Nullable
    @Override
    public Object getData(String dataId) {
        if(PlatformDataKeys.SELECTED_ITEM.is(dataId)) {
            ConsulTreeModel model = getConsulTreeModel();
            if(model != null) {
                return model.getSelectedKeyAndValue();
            }
        }
        return null;
    }
}
