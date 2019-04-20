package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.KeyAndValueEditorPanel;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;

public class NewEntryAction extends AbstractConsulButtonController {
    public NewEntryAction(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        KeyAndValue newNodeParent = getNewNodeParent();
        if (newNodeParent == null) {
            return;
        }

        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(newNodeParent.getFullyQualifiedKey());
        if (keyAndValueFromDialog != null) {
            getTreeModel().addNode(newNodeParent, keyAndValueFromDialog);
            getConsul().insert(keyAndValueFromDialog.getFullyQualifiedKey(), keyAndValueFromDialog.getValue());
        }
    }

    private KeyAndValue getNewNodeParent() {
        KeyAndValue selectedKeyAndValue = getSelectedKeyAndValue();
        if(selectedKeyAndValue == null) {
            return null;
        }
        KeyAndValue parent;
        if(selectedKeyAndValue.isContainer()) {
            parent = selectedKeyAndValue;
        } else {
            parent = ((KVNode) getTreeModel().getNode(selectedKeyAndValue).getParent()).getKeyAndValue();
        }
        return parent;
    }

    private KeyAndValue getKeyAndValueFromDialog(String fqn) {

        KeyAndValueEditorPanel keyAndValuePanel = new KeyAndValueEditorPanel(fqn);

        DialogBuilder builder = new DialogBuilder()
                .title("Create a new entry")
                .centerPanel(keyAndValuePanel);
        builder.setPreferredFocusComponent(keyAndValuePanel.getKeyTextField());
        if(builder.showAndGet()) {
            return keyAndValuePanel.getKeyAndValue();
        }
        return null;
    }
}
