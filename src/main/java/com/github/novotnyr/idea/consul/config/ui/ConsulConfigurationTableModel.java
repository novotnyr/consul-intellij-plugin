package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.intellij.icons.AllIcons;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConsulConfigurationTableModel extends AbstractTableModel {
    public enum Column {
        HOST("Host"),
        PORT("Port"),
        ACL_TOKEN("ACL Token"),
        DATACENTER("Datacenter"),
        USER("User"),
        PASSWORD("Password");

        private String description;

        Column(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final List<Column> COLUMNS = Arrays.asList(Column.values());

    private List<ConsulConfiguration> configurationList = new ArrayList<>();

    public ConsulConfigurationTableModel() {
        ConsulConfiguration cfg = new ConsulConfiguration("localhost", 8500);
        this.configurationList.add(cfg);
    }

    @Override
    public int getRowCount() {
        return this.configurationList.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConsulConfiguration consulConfiguration = configurationList.get(rowIndex);
        Column column = COLUMNS.get(columnIndex);
        switch (column) {
            case HOST:
                return new Host(consulConfiguration);
            case PORT:
                return new Port(consulConfiguration);
            case ACL_TOKEN:
                return consulConfiguration.getAclToken();
            case DATACENTER:
                return consulConfiguration.getDatacenter();
            case USER:
                return consulConfiguration.getUser();
            case PASSWORD:
                return new Password(consulConfiguration.getPassword());
        }
        return null;
    }

    @Override
    public String getColumnName(int columnIndex) {
        Column column = COLUMNS.get(columnIndex);
        return column.getDescription();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Column column = COLUMNS.get(columnIndex);
        switch (column) {
            case HOST:
                return Host.class;
            case PORT:
                return Port.class;
            case ACL_TOKEN:
                return String.class;
            case DATACENTER:
                return String.class;
            case USER:
                return String.class;
            case PASSWORD:
                return Password.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public List<ConsulConfiguration> getConfigurationList() {
        return Collections.unmodifiableList(this.configurationList);
    }

    public void add(ConsulConfiguration consulConfiguration) {
        this.configurationList.add(consulConfiguration);
        fireTableDataChanged();
    }

    public void addAll(List<ConsulConfiguration> consulConfigurationList) {
        this.configurationList.addAll(consulConfigurationList);
        fireTableDataChanged();
    }

    public ConsulConfiguration get(int index) {
        return this.configurationList.get(index);
    }

    public void remove(int rowIndex) {
        this.configurationList.remove(rowIndex);
        fireTableDataChanged();
    }

    public void setConsulConfigurations(Collection<? extends ConsulConfiguration> configurations) {
        this.configurationList.clear();
        this.configurationList.addAll(configurations);
    }

    public void refresh() {
        this.fireTableDataChanged();
    }

    public static class Password {
        private String password;

        public Password(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public boolean isPresent() {
            return password != null && password.length() > 0;
        }
    }

    public static class Host {
        private String host;

        private boolean usingTls;

        public Host(ConsulConfiguration consulConfiguration) {
            this.host = consulConfiguration.getHost();
            this.usingTls = consulConfiguration.isUsingTls();
        }

        public String getHost() {
            return host;
        }

        public boolean isUsingTls() {
            return usingTls;
        }
    }

    public static class Port {
        private final ConsulConfiguration consulConfiguration;

        public Port(ConsulConfiguration consulConfiguration) {
            this.consulConfiguration = consulConfiguration;
        }

        public int getPort() {
            return this.consulConfiguration.getPort();
        }

        public boolean isUsingTls() {
            return this.consulConfiguration.isUsingTls();
        }
    }

    public static class PasswordTableCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Password password = (Password) value;
            boolean hasPassword = password != null && password.isPresent();
            return table.getDefaultRenderer(Boolean.class)
                    .getTableCellRendererComponent(table, hasPassword, isSelected, hasFocus, row, column);
        }
    }

    public static class HostTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Host consulHost = (Host) value;
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText(consulHost.getHost());
            if (consulHost.isUsingTls()) {
                label.setIcon(AllIcons.Nodes.Padlock);
            } else {
                label.setIcon(null);
            }
            return label;
        }
    }

    public static class PortTableCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Port port = (Port) value;
            String cell = String.valueOf(port.getPort());
            if (port.isUsingTls()) {
                cell = cell + "" + " (SSL)";
            }
            return table.getDefaultRenderer(String.class)
                    .getTableCellRendererComponent(table, cell, isSelected, hasFocus, row, column);

        }
    }
}
