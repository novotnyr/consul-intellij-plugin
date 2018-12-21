package com.github.novotnyr.idea.consul.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class UnsynchronizedChangesLabelAction extends AnAction implements CustomComponentAction {
    public static final String UNSYNCHED_CHANGES_LABEL_TEXT = "Changes are not synced";

    private JLabel label = new JBLabel("", UIUtil.ComponentStyle.SMALL);

    public void hideText() {
        this.label.setText("");
    }

    public void showText() {
        this.label.setText(UNSYNCHED_CHANGES_LABEL_TEXT);
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(@NotNull Presentation presentation) {
        return this.label;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // do nothing
    }
}
