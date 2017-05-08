package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.config.ui.ConsulConfigurable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;

public class ShowSettingsAction extends AnAction {
    public ShowSettingsAction() {
        super("Settings", "Show Consul Settings", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = CommonDataKeys.PROJECT.getData(anActionEvent.getDataContext());

        ShowSettingsUtil.getInstance().showSettingsDialog(project, ConsulConfigurable.class);
    }
}
