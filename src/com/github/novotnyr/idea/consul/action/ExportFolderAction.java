package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.tree.TreeUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.MessageBus;

import java.nio.charset.Charset;

public class ExportFolderAction extends AbstractEntryAction {
    private ConsulTreeModel treeModel;

    public ExportFolderAction(Consul consul, MessageBus messageBus) {
        super(consul, messageBus, "Export Folder", "Export Folder as JSON", AllIcons.Actions.Export);
    }

    @Override
    protected void onActionPerformed(String fqn, AnActionEvent event) {
        KVNode node = this.treeModel.getNode(this.selectedKeyAndValue);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        FileChooser.chooseFile(descriptor, getProject(event), null, new Consumer<VirtualFile>() {
            @Override
            public void consume(VirtualFile virtualFile) {
                try {
                    WriteAction.run(new ThrowableRunnable<Throwable>() {
                        @Override
                        public void run() throws Throwable {
                            VirtualFile childData = virtualFile.findOrCreateChildData(this, selectedKeyAndValue.getKey() + ".properties");
                            childData.setBinaryContent(export(node));
                        }
                    });

                } catch (Throwable e) {
                    // TODO fixme
                    e.printStackTrace();
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

    public void setTreeModel(ConsulTreeModel treeModel) {
        this.treeModel = treeModel;
    }
}
