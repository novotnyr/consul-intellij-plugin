package com.github.novotnyr.idea.consul.action2;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;

import javax.swing.tree.TreePath;
import java.awt.Rectangle;


public class ConsolidatedNewEntryAction extends AbstractButtonAction {
    private NewEntryAction2 newEntryAction;
    private NewFolderActionButton2 newFolderAction;
    private ConsulTree consulTree;

    public ConsolidatedNewEntryAction(NewEntryAction2 newEntryAction, NewFolderActionButton2 newFolderAction, ConsulTree consulTree, Consul consul) {
        super(consul);
        this.newEntryAction = newEntryAction;
        this.newFolderAction = newFolderAction;
        this.consulTree = consulTree;
    }

    @Override
    public void run(AnActionButton button) {
        JBList<NewEntryType> list = new JBList<>(NewEntryType.FOLDER, NewEntryType.ITEM);
        JBPopup popup = JBPopupFactory.getInstance()
                .createListPopupBuilder(list)
                .setItemChoosenCallback(() -> onPopupListItemChoosenCallback(list))
                .createPopup();

        TreePath leadSelectionPath = consulTree.getSelectionModel().getLeadSelectionPath();
        if(leadSelectionPath != null) {
            int rowForPath = consulTree.getRowForPath(leadSelectionPath);
            Rectangle rowBounds = consulTree.getRowBounds(rowForPath);
            popup.show(new RelativePoint(consulTree, rowBounds.getLocation()));
        } else {
            final RelativePoint popupPoint = button.getPreferredPopupPoint();
            if (popupPoint != null) {
                popup.show(popupPoint);
            } else {
                popup.showInCenterOf(consulTree);
            }
        }
    }

    private void onPopupListItemChoosenCallback(JBList<NewEntryType> list) {
        switch (list.getSelectedValue()) {
            case FOLDER:
                newFolderAction.run(null);
                return;
            case ITEM:
                newEntryAction.run(null);
                return;
            default:
                throw new IllegalStateException("Unknown action for new entry");
        }
    }

    @Override
    public boolean isEnabled(AnActionEvent event) {
        boolean enabled = super.isEnabled(event);
        this.newEntryAction.isEnabled(event);
        this.newFolderAction.isEnabled(event);
        return enabled;
    }

    public enum NewEntryType {
        FOLDER("New folder"),
        ITEM("New entry");

        private String description;

        NewEntryType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }
}
