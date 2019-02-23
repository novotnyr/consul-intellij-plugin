package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.config.ConsulConfigurationPersistentService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseEvent;

public class ConsulConfigurable implements Configurable {
    private JBTable configurationTable;

    private ConsulConfigurationTableModel configurationTableModel;

    private ConsulConfigurationPersistentService consulConfigurationPersistentService;

    @Nls
    @Override
    public String getDisplayName() {
        return "Consul";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.consulConfigurationPersistentService = ServiceManager.getService(ConsulConfigurationPersistentService.class);

        this.configurationTableModel = new ConsulConfigurationTableModel();
        this.configurationTableModel.setConsulConfigurations(this.consulConfigurationPersistentService.getConsulConfigurationList());

        this.configurationTable = new JBTable(this.configurationTableModel);
        this.configurationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.configurationTable.setDefaultRenderer(ConsulConfigurationTableModel.Password.class, new ConsulConfigurationTableModel.PasswordTableCellRenderer());
        this.configurationTable.setDefaultRenderer(ConsulConfigurationTableModel.Host.class, new ConsulConfigurationTableModel.HostTableCellRenderer());
        installDoubleClickListener(this.configurationTable);

        JPanel panelForTable =  ToolbarDecorator.createDecorator(this.configurationTable)
                .setAddAction(this::onAddAction)
                .setRemoveAction(this::onRemoveAction)
                .disableUpDownActions()
                .createPanel();
        return panelForTable;
    }

    private void installDoubleClickListener(JBTable table) {
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent mouseEvent) {
                onConfigurationTableDoubleClick(mouseEvent);
                return true;
            }
        }.installOn(table);
    }

    private void onConfigurationTableDoubleClick(MouseEvent mouseEvent) {
        int selectedRow = this.configurationTable.getSelectedRow();
        if(selectedRow < 0) {
            return;
        }
        ConsulConfiguration selectecConfiguration = this.configurationTableModel.get(selectedRow);
        ConsulConfigurationDialog dialog = new ConsulConfigurationDialog(selectecConfiguration);
        if(dialog.showAndGet()) {
            this.configurationTableModel.fireTableDataChanged();
        }
    }

    private void onAddAction(AnActionButton anActionButton) {
        ConsulConfigurationDialog dialog = new ConsulConfigurationDialog();
        if(dialog.showAndGet()) {
            ConsulConfiguration consulConfiguration = dialog.getConsulConfiguration();
            this.configurationTableModel.add(consulConfiguration);
        }
    }

    private void onRemoveAction(AnActionButton anActionButton) {
        int selectedRowIndex = this.configurationTable.getSelectedRow();
        this.configurationTableModel.remove(selectedRowIndex);
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        this.consulConfigurationPersistentService.setConsulConfigurationList(this.configurationTableModel.getConfigurationList());
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public void disposeUIResources() {
        // do nothing
    }

    @Override
    public void reset() {
        // do nothing
    }
}
