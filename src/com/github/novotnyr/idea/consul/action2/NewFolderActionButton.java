package com.github.novotnyr.idea.consul.action2;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;

import javax.swing.JTextField;

public class NewFolderActionButton extends AbstractButtonAction {
    public NewFolderActionButton(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        JTextField folderNameTextField = new JTextField();
        DialogBuilder builder = new DialogBuilder()
                .title("New Folder name")
                .centerPanel(folderNameTextField);
        builder.setPreferredFocusComponent(folderNameTextField);
        if (builder.showAndGet()) {
            String fullyQualifiedPath = getFqn() + folderNameTextField.getText();
            getConsul().mkdir(fullyQualifiedPath);
            getTreeModel().addNode(getSelectedKeyAndValue(), new KeyAndValue(fullyQualifiedPath));
        }
    }
}
