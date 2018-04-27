package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConsulConfigurationDialog extends DialogWrapper {

    private ConsulHostPanel consulHostPanel;

    private ConsulConfiguration consulConfiguration;

    protected ConsulConfigurationDialog() {
        super(false);
        init();
        this.consulConfiguration = new ConsulConfiguration();
        setTitle("Add Consul Host");
    }

    protected ConsulConfigurationDialog(ConsulConfiguration consulConfiguration) {
        super(false);
        this.consulConfiguration = consulConfiguration;
        init();
        setTitle("Edit Consul Host");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (consulConfiguration != null) {
            return consulHostPanel = new ConsulHostPanel(this.consulConfiguration);
        } else {
            return consulHostPanel = new ConsulHostPanel();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if(this.consulHostPanel.getHostTextField().getText().isEmpty()) {
            return new ValidationInfo("Host cannot be empty", this.consulHostPanel.getHostTextField());
        }
        if(this.consulHostPanel.getPortTextField().getText().isEmpty()) {
            return new ValidationInfo("Port cannot be empty", this.consulHostPanel.getPortTextField());
        }
        if(! isInteger(this.consulHostPanel.getPortTextField())) {
            return new ValidationInfo("Port must be a numeric value", this.consulHostPanel.getPortTextField());
        }

        return null;
    }

    private boolean isInteger(JTextField field) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void doOKAction() {
        getConsulConfiguration();

        super.doOKAction();
    }

    public ConsulConfiguration getConsulConfiguration() {
        ConsulHostPanel p = this.consulHostPanel;
        this.consulConfiguration.setHost(p.getHostTextField().getText());
        this.consulConfiguration.setPort(Integer.parseInt(p.getPortTextField().getText()));

        this.consulConfiguration.setAclToken(getValue(p.getAclTokenTextField()));
        this.consulConfiguration.setDatacenter(getValue(p.getDatacenterTextField()));
        this.consulConfiguration.setUser(getValue(p.getUserTextField()));
        this.consulConfiguration.setPassword(getValue(p.getPasswordTextField()));

        return this.consulConfiguration;
    }

    private String getValue(JTextField textField) {
        String text = textField.getText();
        if(text.isEmpty()) {
            return null;
        }
        if(textField instanceof JPasswordField) {
            return new String(((JPasswordField) textField).getPassword());
        }
        return text;
    }
}
