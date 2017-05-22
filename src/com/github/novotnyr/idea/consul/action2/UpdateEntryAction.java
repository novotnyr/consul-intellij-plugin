package com.github.novotnyr.idea.consul.action2;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.ui.KeyAndValueEditorPanel;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;

public class UpdateEntryAction extends AbstractButtonAction {
    public UpdateEntryAction(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        KeyAndValue keyAndValueFromDialog = getKeyAndValueFromDialog(getFqn());
        if (keyAndValueFromDialog != null) {
            update(keyAndValueFromDialog);
        }
    }

    public void update(KeyAndValue updatedKeyAndValue) {
        String fqnKey = updatedKeyAndValue.getFullyQualifiedKey();
        String value = updatedKeyAndValue.getValue();

        getConsul().update(fqnKey, value);
        getTreeModel().updateNode(updatedKeyAndValue);
    }


    private KeyAndValue getKeyAndValueFromDialog(String fqn) {
        KeyAndValueEditorPanel keyAndValuePanel = new KeyAndValueEditorPanel(getSelectedKeyAndValue());

        DialogBuilder builder = new DialogBuilder()
                .title("Update an entry")
                .centerPanel(keyAndValuePanel);
        builder.setPreferredFocusComponent(keyAndValuePanel.getKeyTextField());
        if(builder.showAndGet()) {
            return keyAndValuePanel.getKeyAndValue();
        }
        return null;
    }
}
