package com.github.novotnyr.idea.consul.action2;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AnActionButton;

import javax.swing.JLabel;

public class DeleteEntryAction extends AbstractButtonAction {
    public DeleteEntryAction(Consul consul) {
        super(consul);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        String fqn = getFqn();
        if(confirmInDialog(fqn)) {
            getConsul().delete(fqn);
            getTreeModel().remove(new KeyAndValue(fqn));
        }
    }

    private boolean confirmInDialog(String fqn) {
        JLabel keyLabel = new JLabel(fqn);
        DialogBuilder builder = new DialogBuilder()
                .title("Delete an entry")
                .centerPanel(keyLabel);

        int result = Messages.showYesNoDialog("Delete the entry " + fqn + "?",
                "Delete Entry",
                Messages.getQuestionIcon());
        return result == Messages.YES;
    }
}
