package com.github.novotnyr.idea.consul.config.ui;

import com.github.novotnyr.idea.consul.Topics;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.config.PluginSettings;
import com.github.novotnyr.idea.consul.scheduling.ui.PeriodicCheckSettingsPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

public class ConsulConfigurable implements Configurable {
    private final PluginSettings pluginSettings = PluginSettings.getInstance();

    private JBTable configurationTable;

    private ConsulConfigurationTableModel configurationTableModel;

    private PeriodicCheckSettingsPanel periodicCheckSettingsPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "Consul";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.configurationTableModel = new ConsulConfigurationTableModel();
        this.configurationTableModel.setConsulConfigurations(this.pluginSettings.getFullConsulConfigurations());

        this.configurationTable = new JBTable(this.configurationTableModel);
        this.configurationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.configurationTable.setDefaultRenderer(ConsulConfigurationTableModel.Password.class, new ConsulConfigurationTableModel.PasswordTableCellRenderer());
        this.configurationTable.setDefaultRenderer(ConsulConfigurationTableModel.Host.class, new ConsulConfigurationTableModel.HostTableCellRenderer());
        this.configurationTable.setDefaultRenderer(ConsulConfigurationTableModel.Port.class, new ConsulConfigurationTableModel.PortTableCellRenderer());
        installDoubleClickListener(this.configurationTable);

        JPanel panelForTable =  ToolbarDecorator.createDecorator(this.configurationTable)
                .setAddAction(this::onAddAction)
                .setRemoveAction(this::onRemoveAction)
                .disableUpDownActions()
                .createPanel();

        this.periodicCheckSettingsPanel = new PeriodicCheckSettingsPanel();
        this.periodicCheckSettingsPanel.setPeriodInSeconds(this.pluginSettings.getRemoteConsulTreeRefreshInterval());

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.add(panelForTable, BorderLayout.CENTER);
        rootPanel.add(this.periodicCheckSettingsPanel.get(), BorderLayout.PAGE_END);

        return rootPanel;
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
        this.pluginSettings.setFullConsulConfigurations(this.configurationTableModel.getConfigurationList());

        if (this.periodicCheckSettingsPanel.isPeriodicalCheckEnabled()) {
            int period = this.periodicCheckSettingsPanel.getPeriodInSeconds();
            this.pluginSettings.setRemoteConsulTreeRefreshInterval(period);
        } else {
            this.pluginSettings.setRemoteConsulTreeRefreshInterval(PluginSettings.NO_REFRESH);
        }
        notifyMessageBusConsulConfigurableChanged();
    }

    private void notifyMessageBusConsulConfigurableChanged() {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.syncPublisher(Topics.PluginConfigurationChanged.PLUGIN_CONFIGURATION_CHANGED).consulPluginConfigurationChanged();
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
