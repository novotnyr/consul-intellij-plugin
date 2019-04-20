package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.util.ClipboardKeyAndValueParser;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CopyProvider;
import com.intellij.ide.PasteProvider;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTree;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class ConsulTree extends Tree implements DataProvider, PasteProvider, CopyProvider {
    private boolean keyValuesVisible;

    public ConsulTree() {
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                setIcon(value);
                if(! keyValuesVisible) {
                    defaultRender(tree, value, selected, expanded, leaf, row, hasFocus);
                    return;
                }
                if(value instanceof KVNode) {
                    KeyAndValue keyAndValue = ((KVNode) value).getKeyAndValue();
                    if(keyAndValue instanceof RootKeyAndValue) {
                        defaultRender(tree, value, selected, expanded, leaf, row, hasFocus);
                    } else {
                        if (keyAndValue.isContainer()) {
                            append(keyAndValue.getKey());
                        } else {
                            append(keyAndValue.getKey());
                            String first = StringUtil.first(keyAndValue.getValue(), 32, true);
                            append(" " + first, SimpleTextAttributes.GRAYED_ATTRIBUTES);
                        }
                    }
                } else {
                    defaultRender(tree, value, selected, expanded, leaf, row, hasFocus);
                }
            }

            private void setIcon(Object value) {
                if(isContainer(value)) {
                    this.setIcon(AllIcons.Nodes.Folder);
                } else {
                    this.setIcon(AllIcons.FileTypes.Any_type);
                }
            }

            private void defaultRender(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                String text = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
                append(text);
            }
        });
    }

    private boolean isContainer(Object value) {
        return (value instanceof KVNode) && ((KVNode) value).getKeyAndValue().isContainer();
    }

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
        KeyAndValue selectedKeyAndValue = getSelectedKeyAndValue();
        ClipboardKeyAndValueParser clipboardParser = new ClipboardKeyAndValueParser(selectedKeyAndValue);
        for (KeyAndValue pastedKv : clipboardParser.parseClipboard(clipboardContents)) {
            getConsulTreeModel().addNode(selectedKeyAndValue, pastedKv);
            getConsulTreeModel().getConsul().insert(pastedKv.getFullyQualifiedKey(), pastedKv.getValue());
        }
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

    public void setKeyValuesVisible(boolean keyValuesVisible) {
        this.keyValuesVisible = keyValuesVisible;
    }

    public boolean getKeyValuesVisible() {
        return this.keyValuesVisible;
    }
}
