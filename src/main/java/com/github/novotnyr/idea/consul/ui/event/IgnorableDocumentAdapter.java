package com.github.novotnyr.idea.consul.ui.event;

import com.intellij.ui.DocumentAdapter;

import javax.swing.event.DocumentEvent;

public class IgnorableDocumentAdapter extends DocumentAdapter {
    private boolean ignoringEvents;

    @Override
    protected final void textChanged(DocumentEvent documentEvent) {
        if (ignoringEvents) {
            return;
        }
        doTextChanged(documentEvent);
    }

    protected void doTextChanged(DocumentEvent documentEvent) {
        // empty code
    }

    public IgnorableDocumentAdapter enableEventHandling() {
        setIgnoringEvents(false);
        return this;
    }

    public IgnorableDocumentAdapter disableEventHandling() {
        setIgnoringEvents(true);
        return this;
    }

    public void setIgnoringEvents(boolean ignoringEvents) {
        this.ignoringEvents = ignoringEvents;
    }
}
