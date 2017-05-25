package com.github.novotnyr.idea.consul.tree;

import com.intellij.ide.CopyProvider;
import com.intellij.ide.PasteProvider;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class ConsulTree extends Tree implements DataProvider, PasteProvider, CopyProvider {
    public ConsulTreeModel getConsulTreeModel() {
        return (ConsulTreeModel) super.getModel();
    }

    @Nullable
    @Override
    public Object getData(String dataId) {
        if(PlatformDataKeys.SELECTED_ITEM.is(dataId)) {
            return getSelectedKeyAndValue();
        }
        if(PlatformDataKeys.PASTE_PROVIDER.is(dataId)) {
            return this;
        }
        if(PlatformDataKeys.COPY_PROVIDER.is(dataId)) {
            return this;
        }
        return null;
    }

    @Override
    public void performPaste(@NotNull DataContext dataContext) {
        String clipboardContents
                = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        if (clipboardContents == null) {
            return;
        }
        String[] split = clipboardContents.split("=", 2);
        KeyAndValue selectedKeyAndValue = getSelectedKeyAndValue();
        KeyAndValue newKeyAndValue = new KeyAndValue(selectedKeyAndValue.getFullyQualifiedKey() + split[0], split[1]);

        getConsulTreeModel().addNode(selectedKeyAndValue, newKeyAndValue);
        getConsulTreeModel().getConsul().insert(newKeyAndValue.getFullyQualifiedKey(), newKeyAndValue.getValue());
    }

    protected KeyAndValue getSelectedKeyAndValue() {
        ConsulTreeModel model = getConsulTreeModel();
        if (model == null) {
            return null;
        }
        return model.getSelectedKeyAndValue();
    }

    @Override
    public boolean isPastePossible(@NotNull DataContext dataContext) {
        boolean isDirectory = getSelectedKeyAndValue() != null && getSelectedKeyAndValue().isContainer();

        String clipboardContents
                = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        return isDirectory && clipboardContents != null && clipboardContents.contains("=");
    }

    @Override
    public boolean isPasteEnabled(@NotNull DataContext dataContext) {
        return isPastePossible(dataContext);
    }

    @Override
    public void performCopy(@NotNull DataContext dataContext) {
        KeyAndValue selectedKeyAndValue = getSelectedKeyAndValue();
        String exportedString;
        if (selectedKeyAndValue.isContainer()) {
            exportedString = selectedKeyAndValue.getKey();
        } else {
            exportedString = selectedKeyAndValue.getKey() + "=" + selectedKeyAndValue.getValue();
        }
        CopyPasteManager.getInstance().setContents(new StringSelection(exportedString));
    }

    @Override
    public boolean isCopyEnabled(@NotNull DataContext dataContext) {
        return getSelectedKeyAndValue() != null;
    }

    @Override
    public boolean isCopyVisible(@NotNull DataContext dataContext) {
        return isCopyEnabled(dataContext);
    }
}
