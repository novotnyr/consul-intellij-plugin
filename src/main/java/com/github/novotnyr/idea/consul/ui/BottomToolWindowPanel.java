package com.github.novotnyr.idea.consul.ui;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Component;

public class BottomToolWindowPanel extends SimpleToolWindowPanel {
    public BottomToolWindowPanel() {
        super(true);
    }

    @Override
    public void add(@NotNull Component comp, Object constraints) {
        if(this.myVertical && BorderLayout.NORTH.equals(constraints)) {
            super.add(comp, BorderLayout.SOUTH);
        } else {
            super.add(comp, constraints);
        }
    }
}
