package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.tree.TreePath;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;


public class ConsolidatedNewEntryAction extends AbstractConsulButtonController {
    private NewEntryAction newEntryAction;
    private NewFolderActionButton newFolderAction;
    private ConsulTree consulTree;

    public ConsolidatedNewEntryAction(NewEntryAction newEntryAction, NewFolderActionButton newFolderAction, ConsulTree consulTree, Consul consul) {
        super(consul);
        this.newEntryAction = newEntryAction;
        this.newFolderAction = newFolderAction;
        this.consulTree = consulTree;
    }

    @Override
    public void run(AnActionButton button) {
        JBPopup popup = JBPopupFactory.getInstance()
                                      .createPopupChooserBuilder(List.of(NewEntryType.ITEM, NewEntryType.FOLDER))
                                      .setTitle("Create New")
                                      .setItemChosenCallback(this::onPopupListItemChoosenCallback)
                                      .createPopup();


        TreePath leadSelectionPath = consulTree.getSelectionModel().getLeadSelectionPath();
        if(leadSelectionPath != null) {
            int rowForPath = consulTree.getRowForPath(leadSelectionPath);
            Rectangle rowBounds = consulTree.getRowBounds(rowForPath);
            Point location = rowBounds.getLocation();
            location.translate(0, (int) rowBounds.getHeight());
            popup.show(new RelativePoint(consulTree, location));
        } else {
            final RelativePoint popupPoint = button.getPreferredPopupPoint();
            if (popupPoint != null) {
                popup.show(popupPoint);
            } else {
                popup.showInCenterOf(consulTree);
            }
        }
    }

    private void onPopupListItemChoosenCallback(NewEntryType selectedValue) {
        switch (selectedValue) {
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
