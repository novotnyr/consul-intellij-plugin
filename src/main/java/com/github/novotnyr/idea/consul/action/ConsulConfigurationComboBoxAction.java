package com.github.novotnyr.idea.consul.action;

import com.github.novotnyr.idea.consul.Topics;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.config.PluginSettings;
import com.github.novotnyr.idea.consul.ui.AbstractComboBoxAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;

import java.util.List;

public class ConsulConfigurationComboBoxAction extends AbstractComboBoxAction<ConsulConfiguration> {
    private static final Logger LOG = Logger.getInstance(ConsulConfigurationComboBoxAction.class);

    private MessageBus messageBus;

    public ConsulConfigurationComboBoxAction(MessageBus messageBus) {
        this.messageBus = messageBus;
    }


    @Override
    public void update(AnActionEvent e) {
        refreshItems();
    }

    public void refreshItems() {
        PluginSettings pluginSettings = PluginSettings.getInstance();

        List<ConsulConfiguration> consulConfigurations = pluginSettings.getConsulConfigurationList();
        ConsulConfiguration selection = getSelection();
        if(selection == null) {
            selection = consulConfigurations.isEmpty() ? null : consulConfigurations.get(0);
        }
        setItems(consulConfigurations, selection);
    }

    @Override
    protected void update(ConsulConfiguration consulConfiguration, Presentation presentation, boolean popup) {
        if(popup) {
            presentation.setText(toString(consulConfiguration));
        } else {
            presentation.setText(toString(getSelection()));
        }
    }

    private String toString(ConsulConfiguration consulConfiguration) {
        if (consulConfiguration == null) {
            return "- no Consul configuration -";
        }
        StringBuilder buffer = new StringBuilder()
                .append(consulConfiguration.getHost())
                .append(":")
                .append(consulConfiguration.getPort());
        if(consulConfiguration.getDatacenter() != null) {
            buffer.append(" [").append(consulConfiguration.getDatacenter()).append("]");
        }
        return buffer.toString();
    }

    @Override
    protected boolean selectionChanged(ConsulConfiguration consulConfiguration) {
        boolean selectionChanged = !getSelection().equals(consulConfiguration);
        if(selectionChanged) {
            fireSelectionChanged(consulConfiguration);
        }
        return selectionChanged;
    }

    private void fireSelectionChanged(ConsulConfiguration newSelection) {
        // we cannot rely on the getSelection(), since it is modified only
        // after selectionChanged() method successfully completes. (see parent implementation)
        messageBus
                .syncPublisher(Topics.ConsulConfigurationChanged.CONSUL_CONFIGURATION_CHANGED_TOPIC)
                .consulConfigurationChanged(newSelection);
    }
}
