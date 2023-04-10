package com.github.novotnyr.idea.consul.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractComboBoxAction<T> extends ComboBoxAction {
    protected List<T> items = new ArrayList<>();

    private T selection;

    private Presentation presentation;

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent jComponent) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        for (T item : this.items) {
            AnAction action = new AnAction() {
                @Override
                public void actionPerformed(AnActionEvent anActionEvent) {
                    if (AbstractComboBoxAction.this.selection != item && AbstractComboBoxAction.this.selectionChanged(item)) {
                        AbstractComboBoxAction.this.selection = item;
                        AbstractComboBoxAction.this.update(item, AbstractComboBoxAction.this.presentation, false);
                    }

                }
            };
            actionGroup.add(action);

            Presentation presentation = action.getTemplatePresentation();
            presentation.setIcon(this.selection == item ? AllIcons.Actions.Checked : null);
            this.update(item, presentation, true);
        }

        return actionGroup;
    }

    public void setItems(List<T> items, @Nullable T selection) {
        this.items = items;
        setSelection(selection);
    }

    public T getSelection() {
        return this.selection;
    }

    public void setSelection(T selection) {
        this.selection = selection;
        if (selection == null && !this.items.isEmpty()) {
            this.selection = this.items.get(0);
        }

        update();
    }

    public void update() {
        Presentation presentation = this.presentation == null ? this.getTemplatePresentation() : this.presentation;
        if (presentation == null) {
            presentation = new Presentation();
        }
        this.update(this.selection, presentation, false);
    }

    @NotNull
    public JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        this.presentation = presentation;
        update();
        JPanel panel = new JPanel(new GridBagLayout());

        JBInsets insets = JBUI.insets(2, 1);
        GridBagConstraints constraints = new GridBagConstraints(0, 0,
                1, 1, 1.0D, 1.0D, 10, 1, insets, 0, 0);

        panel.add(createComboBoxButton(presentation), constraints);
        return panel;
    }

    protected abstract boolean selectionChanged(T item);

    protected abstract void update(T item, Presentation presentation, boolean popup);
}
