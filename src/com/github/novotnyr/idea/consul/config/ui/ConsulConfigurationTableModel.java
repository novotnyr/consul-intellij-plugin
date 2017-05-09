package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;

import javax.swing.table.AbstractTableModel;
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
        ConsulConfiguration cfg2 = new ConsulConfiguration("web", 8500);
        this.configurationList.add(cfg);
        this.configurationList.add(cfg2);
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
                return consulConfiguration.getHost();
            case PORT:
                return consulConfiguration.getPort();
            case ACL_TOKEN:
                return consulConfiguration.getAclToken();
            case DATACENTER:
                return consulConfiguration.getDatacenter();
            case USER:
                return consulConfiguration.getUser();
            case PASSWORD:
                return consulConfiguration.getPassword();
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
                return String.class;
            case PORT:
                return Integer.class;
            case ACL_TOKEN:
                return String.class;
            case DATACENTER:
                return String.class;
            case USER:
                return String.class;
            case PASSWORD:
                return Boolean.class;
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

    public void remove(int rowIndex) {
        this.configurationList.remove(rowIndex);
        fireTableDataChanged();
    }

    public void setConsulConfigurations(Collection<? extends ConsulConfiguration> configurations) {
        this.configurationList.clear();
        this.configurationList.addAll(configurations);
    }

}
