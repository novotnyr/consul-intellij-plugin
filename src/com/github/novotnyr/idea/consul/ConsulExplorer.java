package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.action.ConsulConfigurationComboBoxAction;
import com.github.novotnyr.idea.consul.action.DeleteEntryAction;
import com.github.novotnyr.idea.consul.action.ExportFolderAction;
import com.github.novotnyr.idea.consul.action.NewEntryAction;
import com.github.novotnyr.idea.consul.action.NewFolderAction;
import com.github.novotnyr.idea.consul.action.RefreshTreeAction;
import com.github.novotnyr.idea.consul.action.ShowSettingsAction;
import com.github.novotnyr.idea.consul.action.UpdateEntryAction;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;

import javax.swing.JComponent;

public class ConsulExplorer extends SimpleToolWindowPanel implements Disposable {

    private ConsulPanel consulPanel;

    private Consul consul;

    private NewFolderAction newFolderAction;

    private Project project;

    private DeleteEntryAction deleteEntryAction;

    private NewEntryAction newEntryAction;

    private UpdateEntryAction updateEntryAction;

    private ConsulConfigurationComboBoxAction consulConfigurationComboBoxAction;

    private final MessageBus messageBus;

    private final MessageBusConnection busConnection;

    private ExportFolderAction exportFolderAction;

    public ConsulExplorer(Project project) {
        super(true);

        this.project = project;

        this.messageBus = this.project.getMessageBus();
        this.busConnection = messageBus.connect();

        this.consulConfigurationComboBoxAction = new ConsulConfigurationComboBoxAction(this.project.getMessageBus());
        this.consulConfigurationComboBoxAction.refreshItems();

        this.consul = new Consul(consulConfigurationComboBoxAction.getSelection());

        setToolbar(createToolbarPanel());

        this.consulPanel = new ConsulPanel(this.consul, messageBus);
        this.consulPanel.setTreeValueSelectedListener(this::onTreeValueSelected);

        bindTreeModel();

        setContent(ScrollPaneFactory.createScrollPane(this.consulPanel));

        configureMessageBus();
    }

    private void bindTreeModel() {
        this.newEntryAction.setTreeModel(this.consulPanel.getTreeModel());
        this.newFolderAction.setTreeModel(this.consulPanel.getTreeModel());
        this.deleteEntryAction.setTreeModel(this.consulPanel.getTreeModel());
        this.exportFolderAction.setTreeModel(this.consulPanel.getTreeModel());
        this.updateEntryAction.setTreeModel(this.consulPanel.getTreeModel());
    }

    private void configureMessageBus() {
        this.busConnection.subscribe(Topics.RefreshTree.REFRESH_TREE_TOPIC, new Topics.RefreshTree() {
            @Override
            public void refreshTree() {
                consul.setConfiguration(consulConfigurationComboBoxAction.getSelection());
                consulPanel.refresh();
                bindTreeModel();
            }
        });
        this.busConnection.subscribe(Topics.ConsulConfigurationChanged.CONSUL_CONFIGURATION_CHANGED_TOPIC, new Topics.ConsulConfigurationChanged() {
            @Override
            public void consulConfigurationChanged(ConsulConfiguration newConfiguration) {
                consul.setConfiguration(newConfiguration);
                consulPanel.refresh();
                bindTreeModel();
            }
        });
        this.busConnection.subscribe(Topics.KeyValueChanged.KEY_VALUE_CHANGED, new Topics.KeyValueChanged() {
            @Override
            public void keyValueChanged(KeyAndValue keyAndValue) {
                updateEntryAction.update(keyAndValue);
            }
        });
    }

    private JComponent createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        RefreshTreeAction action = new RefreshTreeAction(this.project.getMessageBus());
        group.add(action);

        this.newFolderAction = new NewFolderAction(this.consul, this.project.getMessageBus());
        group.add(this.newFolderAction);

        this.newEntryAction = new NewEntryAction(this.consul, this.project.getMessageBus());
        group.add(this.newEntryAction);

        this.deleteEntryAction = new DeleteEntryAction(this.consul, this.project.getMessageBus());
        group.add(deleteEntryAction);

        this.updateEntryAction = new UpdateEntryAction(this.consul, this.project.getMessageBus());

        this.exportFolderAction = new ExportFolderAction(this.consul, this.project.getMessageBus());
        group.add(exportFolderAction);

        group.add(consulConfigurationComboBoxAction);

        group.addSeparator();

        group.add(new ShowSettingsAction());

        /*
        AnAction action = CommonActionsManager.getInstance().createExpandAllAction(myTreeExpander, this);
        action.getTemplatePresentation().setDescription(AntBundle.message("ant.explorer.expand.all.nodes.action.description"));
        group.add(action);
        action = CommonActionsManager.getInstance().createCollapseAllAction(myTreeExpander, this);
        action.getTemplatePresentation().setDescription(AntBundle.message("ant.explorer.collapse.all.nodes.action.description"));
        group.add(action);
        group.add(myAntBuildFilePropertiesAction);
        group.add(new ContextHelpAction(HelpID.ANT));
        */

        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("consulToolbar", group, true);
        actionToolBar.setTargetComponent(this.consulPanel);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }

    private void onTreeValueSelected(KeyAndValue keyValue) {
        DataContext dataContext = SimpleDataContext.getSimpleContext("selectedKeyAndValue", keyValue);
        AnActionEvent event = AnActionEvent.createFromDataContext("ConsulExplorer", null, dataContext);
        this.newFolderAction.update(event);
        this.deleteEntryAction.update(event);
        this.newEntryAction.update(event);
        this.updateEntryAction.update(event);
        this.exportFolderAction.update(event);
    }



}
