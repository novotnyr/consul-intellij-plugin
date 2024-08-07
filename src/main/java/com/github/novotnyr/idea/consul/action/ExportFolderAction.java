package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.tree.TreeUtils;
import com.github.novotnyr.idea.consul.util.Icons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import com.intellij.util.messages.MessageBus;

import java.nio.charset.Charset;

public class ExportFolderAction extends AbstractEntryAction {
    private final Logger logger = Logger.getInstance(ExportFolderAction.class);

    private ConsulTreeModel treeModel;

    public ExportFolderAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "Export Folder", "Export Folder as JSON", Icons.export());
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        KVNode node = this.treeModel.getNode(this.selectedKeyAndValue);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        FileChooser.chooseFile(descriptor, getProject(event), null, new Consumer<>() {
            @Override
            public void consume(VirtualFile virtualFile) {
                try {
                    ThrowableComputable<Void, Throwable> saveAction = new ThrowableComputable<>() {
                        @Override
                        public Void compute() throws Throwable {
                            VirtualFile childData = virtualFile.findOrCreateChildData(this, selectedKeyAndValue.getKey() + ".properties");
                            childData.setBinaryContent(export(node));

                            return null;
                        }
                    };

                    WriteCommandAction.runWriteCommandAction(getProject(event), saveAction);
                } catch (Throwable e) {
                    logger.error(e);
                }
            }
        });
    }

    private byte[] export(KVNode node) {
        StringBuilder export = new StringBuilder();
        for (KVNode child : TreeUtils.iterableChildren(node)) {
            KeyAndValue keyAndValue = child.getKeyAndValue();
            export.append(keyAndValue.getKey()).append("=").append(keyAndValue.getValue()).append("\n");
        }
        return export.toString().getBytes(Charset.defaultCharset());
    }

    protected Project getProject(AnActionEvent event) {
        return CommonDataKeys.PROJECT.getData(event.getDataContext());
    }

    @Override
    protected boolean isEnabledForKeyAndValue(KeyAndValue keyAndValue) {
        return keyAndValue.isContainer();
    }

    public void setTreeModel(ConsulTreeModel treeModel) {
        this.treeModel = treeModel;
    }
}
