package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.JBUI;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ConsulHostPanel extends JPanel {
    private ConsulConfiguration consulConfiguration;

    private JLabel hostLabel = new JLabel("Host");

    private JTextField hostTextField = new JTextField();

    private JLabel portLabel = new JLabel("Port");

    private JTextField portTextField = new JTextField();

    private JLabel datacenterLabel = new JLabel("Datacenter");

    private JTextField datacenterTextField = new JTextField();

    private JLabel aclTokenLabel = new JLabel("ACL Token");

    private JTextField aclTokenTextField = new JTextField();

    private JLabel userLabel = new JLabel("User");

    private JTextField userTextField = new JTextField();

    private JLabel passwordLabel = new JLabel("Password");

    private JPasswordField passwordTextField = new JPasswordField();

    public ConsulHostPanel() {
        this(new ConsulConfiguration());
    }

    public ConsulHostPanel(ConsulConfiguration consulConfiguration) {
        this.consulConfiguration = consulConfiguration;

        setLayout(new GridBagLayout());

        GridBagConstraints cLabel = new GridBagConstraints();
        cLabel.fill = GridBagConstraints.NONE;
        cLabel.anchor = GridBagConstraints.LINE_START;
        cLabel.weightx = 0;
        cLabel.gridx = 0;
        cLabel.gridy = 0;
        cLabel.insets = JBUI.insets(5);

        GridBagConstraints cTextField = new GridBagConstraints();
        cTextField.fill = GridBagConstraints.HORIZONTAL;
        cLabel.anchor = GridBagConstraints.LINE_START;
        cTextField.weightx = 1;
        cTextField.gridx = 1;
        cTextField.gridy = 0;
        cTextField.insets = JBUI.insets(5);

        add(this.hostLabel, cLabel);
        add(this.hostTextField, cTextField);

        cLabel.gridy = 1;
        cTextField.gridy = 1;

        add(this.portLabel, cLabel);
        add(this.portTextField, cTextField);

        cLabel.gridy = 2;
        cTextField.gridy = 2;

        add(this.datacenterLabel, cLabel);
        add(this.datacenterTextField, cTextField);

        cLabel.gridy = 3;
        cTextField.gridy = 3;

        add(this.aclTokenLabel, cLabel);
        add(this.aclTokenTextField, cTextField);

        GridBagConstraints c2Columns = new GridBagConstraints();
        c2Columns.fill = GridBagConstraints.HORIZONTAL;
        c2Columns.gridx = 0;
        c2Columns.gridy = 4;
        c2Columns.gridwidth = GridBagConstraints.REMAINDER;

        JPanel separatorPanel = new JPanel();
        separatorPanel.setMaximumSize(new Dimension(0, 0));
        separatorPanel.setBorder(IdeBorderFactory.createTitledBorder("HTTP Basic", false, JBUI.insets(0)));
        add(separatorPanel, c2Columns);

        cLabel.gridy = 5;
        cTextField.gridy = 5;

        add(this.userLabel, cLabel);
        add(this.userTextField, cTextField);

        cLabel.gridy = 6;
        cTextField.gridy = 6;

        add(this.passwordLabel, cLabel);
        add(this.passwordTextField, cTextField);

        bindFromModel();

        setPreferredSize(new Dimension(400, -1));
    }

    private void bindFromModel() {
        this.hostTextField.setText(this.consulConfiguration.getHost());
        this.portTextField.setText(String.valueOf(this.consulConfiguration.getPort()));
        this.datacenterTextField.setText(this.consulConfiguration.getDatacenter());
        this.aclTokenTextField.setText(this.consulConfiguration.getAclToken());
        this.userTextField.setText(this.consulConfiguration.getUser());
        this.passwordTextField.setText(this.consulConfiguration.getPassword());
    }

    public JTextField getHostTextField() {
        return hostTextField;
    }

    public JTextField getPortTextField() {
        return portTextField;
    }

    public JTextField getDatacenterTextField() {
        return datacenterTextField;
    }

    public JTextField getAclTokenTextField() {
        return aclTokenTextField;
    }

    public JTextField getUserTextField() {
        return userTextField;
    }

    public JPasswordField getPasswordTextField() {
        return passwordTextField;
    }
}
