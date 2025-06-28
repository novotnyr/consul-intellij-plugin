package com.github.novotnyr.idea.consul;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ConsulExplorer consulExplorer = new ConsulExplorer(project);

        Content content = ContentFactory.getInstance().createContent(consulExplorer, "", false);
        toolWindow.getContentManager().addContent(content);

        if(toolWindow instanceof ToolWindowEx) {
            ToolWindowEx extendedToolWindow = (ToolWindowEx) toolWindow;
            extendedToolWindow.setAdditionalGearActions(getToolWindowActionGroup(consulExplorer));
        }
    }

    private ActionGroup getToolWindowActionGroup(ConsulExplorer consulExplorer) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new CheckboxAction("Show inline values") {
            @Override
            public boolean isSelected(AnActionEvent anActionEvent) {
                return consulExplorer.getKeyValuesVisible();
            }

            @Override
            public void setSelected(AnActionEvent anActionEvent, boolean state) {
                consulExplorer.setKeyValuesVisible(state);
            }
        });
        return actionGroup;
    }
}
