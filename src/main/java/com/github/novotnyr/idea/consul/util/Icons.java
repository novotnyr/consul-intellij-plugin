package com.github.novotnyr.idea.consul.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public abstract class Icons {
    public static Icon export() {
        return IconLoader.getIcon("/actions/export.png", Icons.class);
    }
}
