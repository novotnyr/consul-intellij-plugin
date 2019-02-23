package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConsulConfigurationDialog extends DialogWrapper {

    private ConsulConfiguration consulConfiguration;

    private JTextField hostTextField;
    private JTextField portTextField;
    private JTextField dataCenterTextField;
    private JTextField aclTokenTextField;
    private JTextField httpBasicUserTextField;
    private JTextField httpBasicPasswordTextField;
    private JCheckBox useTlsCheckBox;
    private JPanel rootPanel;

    protected ConsulConfigurationDialog() {
        super(false);
        init();
        this.consulConfiguration = new ConsulConfiguration();
        setTitle("Add Consul Host");
        bindFromModel();
    }

    protected ConsulConfigurationDialog(ConsulConfiguration consulConfiguration) {
        super(false);
        this.consulConfiguration = consulConfiguration;
        init();
        setTitle("Edit Consul Host");
        bindFromModel();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return this.rootPanel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if(this.hostTextField.getText().isEmpty()) {
            return new ValidationInfo("Host cannot be empty", this.hostTextField);
        }
        if(this.portTextField.getText().isEmpty()) {
            return new ValidationInfo("Port cannot be empty", this.portTextField);
        }
        if(! isInteger(this.portTextField)) {
            return new ValidationInfo("Port must be a numeric value", this.portTextField);
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
        this.consulConfiguration.setHost(hostTextField.getText());
        this.consulConfiguration.setPort(Integer.parseInt(portTextField.getText()));

        this.consulConfiguration.setAclToken(getValue(aclTokenTextField));
        this.consulConfiguration.setDatacenter(getValue(dataCenterTextField));
        this.consulConfiguration.setUser(getValue(httpBasicUserTextField));
        this.consulConfiguration.setPassword(getValue(httpBasicPasswordTextField));

        this.consulConfiguration.setUsingTls(useTlsCheckBox.isSelected());

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

    private void bindFromModel() {
        this.hostTextField.setText(this.consulConfiguration.getHost());
        this.portTextField.setText(String.valueOf(this.consulConfiguration.getPort()));
        this.dataCenterTextField.setText(this.consulConfiguration.getDatacenter());
        this.aclTokenTextField.setText(this.consulConfiguration.getAclToken());
        this.httpBasicUserTextField.setText(this.consulConfiguration.getUser());
        this.httpBasicPasswordTextField.setText(this.consulConfiguration.getPassword());
        this.useTlsCheckBox.setSelected(this.consulConfiguration.isUsingTls());
    }
}
