package com.github.novotnyr.idea.consul.ui;

import com.intellij.openapi.actionSystem.ex.DefaultCustomComponentAction;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;

public class LabelAction extends DefaultCustomComponentAction {

    public LabelAction(String text) {
        super(new JBLabel(text, UIUtil.ComponentStyle.SMALL));
    }

    protected JBLabel getLabel() {
        return (JBLabel) super.createCustomComponent(null);
    }

    public void setLabelText(String text) {
        getLabel().setText(text);
    }
}
