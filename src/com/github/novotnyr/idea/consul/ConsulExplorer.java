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
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Component;

public class ConsulExplorer extends SimpleToolWindowPanel implements Disposable, DataProvider {

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

    private ConsulTree tree;

    private ConsulTreeModel treeModel;

    private KeyAndValuePanel keyAndValuePanel;

    private KeyAndValue selectedKeyAndValue;

    public ConsulExplorer(Project project) {
        super(true);

        this.project = project;

        this.messageBus = this.project.getMessageBus();
        this.busConnection = messageBus.connect();

        this.consulConfigurationComboBoxAction = new ConsulConfigurationComboBoxAction(this.project.getMessageBus());
        this.consulConfigurationComboBoxAction.refreshItems();

        this.consul = new Consul(consulConfigurationComboBoxAction.getSelection());

        setToolbar(createToolbarPanel());

        bindTreeModel();
        configureMessageBus();

        this.tree = new ConsulTree();
        initializeTreeModel();
        TreeUtil.installActions(tree);
        installPopupHandler(tree);

        this.keyAndValuePanel = new KeyAndValuePanel(this.messageBus, treeModel);


        JBSplitter splitter = new JBSplitter(true, 0.8f, 0.1f, 0.9f);
        splitter.setFirstComponent(new JBScrollPane(this.tree));
        splitter.setSecondComponent(this.keyAndValuePanel);
        splitter.setHonorComponentsMinimumSize(true);

        setContent(ScrollPaneFactory.createScrollPane(splitter));

    }

    private void bindTreeModel() {
        this.newEntryAction.setTreeModel(this.treeModel);
        this.newFolderAction.setTreeModel(this.treeModel);
        this.deleteEntryAction.setTreeModel(this.treeModel);
        this.exportFolderAction.setTreeModel(this.treeModel);
        this.updateEntryAction.setTreeModel(this.treeModel);
    }

    private void configureMessageBus() {
        this.busConnection.subscribe(Topics.RefreshTree.REFRESH_TREE_TOPIC, new Topics.RefreshTree() {
            @Override
            public void refreshTree() {
                consul.setConfiguration(consulConfigurationComboBoxAction.getSelection());
                refresh();
                bindTreeModel();
            }
        });
        this.busConnection.subscribe(Topics.ConsulConfigurationChanged.CONSUL_CONFIGURATION_CHANGED_TOPIC, new Topics.ConsulConfigurationChanged() {
            @Override
            public void consulConfigurationChanged(ConsulConfiguration newConfiguration) {
                consul.setConfiguration(newConfiguration);
                refresh();
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
        actionToolBar.setTargetComponent(this.tree);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }

    private void installPopupHandler(Tree tree) {
        tree.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(final Component comp, final int x, final int y) {
                popupInvoked(comp, x, y);
            }
        });
    }

    private void popupInvoked(final Component comp, final int x, final int y) {
        KeyAndValue keyAndValue = null;
        final TreePath path = tree.getSelectionPath();
        if (path != null) {
            KVNode node = (KVNode) path.getLastPathComponent();
            if (node != null) {
                keyAndValue = node.getKeyAndValue();
            }
        }
        DefaultActionGroup group = new DefaultActionGroup();
        NewEntryAction newEntryAction = new NewEntryAction(this.consul, this.messageBus);
        newEntryAction.setSelectedKeyAndValue(keyAndValue);
        newEntryAction.setTreeModel(this.treeModel);

        group.add(newEntryAction);

        NewFolderAction newFolderAction = new NewFolderAction(this.consul, this.messageBus);
        newFolderAction.setSelectedKeyAndValue(keyAndValue);
        newFolderAction.setTreeModel(this.treeModel);

        group.add(newFolderAction);

        ActionPopupMenu popupMenu = ActionManager.getInstance()
                .createActionPopupMenu("ConsulTreePopup", group);
        popupMenu.getComponent().show(comp, x, y);
    }

    private void initializeTreeModel() {
        treeModel = new ConsulTreeModel(tree, this.consul);
        treeModel.setOnValueSelectedListener(this::treeValueSelected);
        tree.setModel(treeModel);
        tree.addTreeWillExpandListener(this.treeModel);
        tree.addTreeSelectionListener(this.treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void treeValueSelected(KeyAndValue kv) {
        this.keyAndValuePanel.setKeyAndValue(kv);
        this.selectedKeyAndValue = kv;
    }

    public void refresh() {
        initializeTreeModel();
    }

    @Nullable
    @Override
    public Object getData(String s) {
        if(PlatformDataKeys.SELECTED_ITEM.is(s)) {
            return this.selectedKeyAndValue;
        } else {
            System.out.println("Unknown data " + s);
        }
        return null;
    }

}
