package com.github.novotnyr.idea.consul.scheduling.ui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class PeriodicCheckSettingsPanel {
    private JPanel rootPanel;
    private JCheckBox periodicallyCheckRemoteConsulCheckBox;
    private JSpinner periodSpinner;

    public PeriodicCheckSettingsPanel() {
        this.periodicallyCheckRemoteConsulCheckBox.addItemListener(e -> periodSpinner.setEnabled(periodicallyCheckRemoteConsulCheckBox.isSelected()));
    }

    public JPanel get() {
        return this.rootPanel;
    }

    public boolean isPeriodicalCheckEnabled() {
        return this.periodicallyCheckRemoteConsulCheckBox.isSelected();
    }

    public int getPeriodInSeconds() {
        return (int) this.periodSpinner.getValue();
    }

    public void setPeriodInSeconds(int periodInSeconds) {
        if (periodInSeconds < 0) {
            this.periodicallyCheckRemoteConsulCheckBox.setSelected(false);
            this.periodSpinner.setEnabled(false);
        } else {
            this.periodicallyCheckRemoteConsulCheckBox.setSelected(true);
            this.periodSpinner.setEnabled(true);
            this.periodSpinner.setValue(periodInSeconds);
        }
    }
}

