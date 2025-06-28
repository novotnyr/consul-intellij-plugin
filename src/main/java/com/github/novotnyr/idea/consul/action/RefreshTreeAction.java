package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Topics;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBus;

public class RefreshTreeAction extends com.intellij.ide.actions.RefreshAction {
    private MessageBus messageBus;

    public RefreshTreeAction(MessageBus messageBus) {
        super("Refresh", "Check for new commits and refresh Log if necessary", AllIcons.Actions.Refresh);
        this.messageBus = messageBus;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        refreshTree();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(true);
    }

    public void refreshTree() {
        this.messageBus.syncPublisher(Topics.RefreshTree.REFRESH_TREE_TOPIC).refreshTree();
    }

    public static NotificationAction createNotificationAction() {
        var appMessageBus = ApplicationManager.getApplication().getMessageBus();
        RefreshTreeAction refreshTreeAction = new RefreshTreeAction(appMessageBus);
        return NotificationAction.createSimpleExpiring("Refresh", refreshTreeAction::refreshTree);
    }
}