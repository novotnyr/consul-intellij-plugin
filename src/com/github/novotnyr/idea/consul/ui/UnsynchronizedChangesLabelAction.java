package com.github.novotnyr.idea.consul.ui;

public class UnsynchronizedChangesLabelAction extends LabelAction {
    public static final String UNSYNCHED_CHANGES_LABEL_TEXT = "Changes are not synced";

    public UnsynchronizedChangesLabelAction() {
        super("");
    }

    public void hideText() {
        getLabel().setText("");
    }

    public void showText() {
        getLabel().setText(UNSYNCHED_CHANGES_LABEL_TEXT);
    }
}
