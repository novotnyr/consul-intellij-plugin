package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;

import javax.swing.JTextField;

public class NewFolderActionButton extends AbstractConsulButtonController {
    public NewFolderActionButton(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        KeyAndValue selectedKeyAndValue = getSelectedKeyAndValue();
        if(selectedKeyAndValue == null) {
            return;
        }
        KeyAndValue parent;
        if(selectedKeyAndValue.isContainer()) {
            parent = selectedKeyAndValue;
        } else {
            parent = ((KVNode) getTreeModel().getNode(selectedKeyAndValue).getParent()).getKeyAndValue();
        }

        JTextField folderNameTextField = new JTextField();
        DialogBuilder builder = new DialogBuilder()
                .title("New Folder name")
                .centerPanel(folderNameTextField);
        builder.setPreferredFocusComponent(folderNameTextField);
        if (builder.showAndGet()) {
            String fullyQualifiedPath = parent.getFullyQualifiedKey() + folderNameTextField.getText();
            getConsul().mkdir(fullyQualifiedPath);
            getTreeModel().addNode(parent, new KeyAndValue(fullyQualifiedPath));
        }
    }
}
