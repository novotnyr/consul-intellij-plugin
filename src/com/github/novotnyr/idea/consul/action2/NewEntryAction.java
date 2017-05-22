package com.github.novotnyr.idea.consul.action2;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.KeyAndValueEditorPanel;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;

public class NewEntryAction extends AbstractButtonAction {
    public NewEntryAction(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(getFqn());
        if (keyAndValueFromDialog != null) {
            getTreeModel().addNode(getSelectedKeyAndValue(), keyAndValueFromDialog);
            getConsul().insert(keyAndValueFromDialog.getFullyQualifiedKey(), keyAndValueFromDialog.getValue());
        }
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
