package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.action.ConsolidatedNewEntryAction;
import com.github.novotnyr.idea.consul.action.ConsulConfigurationComboBoxAction;
import com.github.novotnyr.idea.consul.action.DeleteEntryAction;
import com.github.novotnyr.idea.consul.action.ExportFolderAction;
import com.github.novotnyr.idea.consul.action.NewEntryAction;
import com.github.novotnyr.idea.consul.action.NewFolderActionButton;
import com.github.novotnyr.idea.consul.action.RefreshTreeAction;
import com.github.novotnyr.idea.consul.action.ShowSettingsAction;
import com.github.novotnyr.idea.consul.action.UpdateEntryAction;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.scheduling.ConsulPeriodicStatusCheckController;
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.tree.TreeUtils;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class ConsulExplorer extends SimpleToolWindowPanel implements Disposable, ConsulTreeModel.TreeModelLoadingListener {

    private Consul consul;

    private Project project;

    private UpdateEntryAction updateEntryAction;

    private ConsulConfigurationComboBoxAction consulConfigurationComboBoxAction;

    private final MessageBus messageBus;

    private final MessageBusConnection busConnection;

    private ExportFolderAction exportFolderAction;

    private ConsulTree tree;

    private ConsulTreeModel treeModel;

    private TreeExpander treeExpander;

    private KeyAndValuePanel keyAndValuePanel;

    private RefreshTreeAction refreshTreeAction;

    private ShowSettingsAction showSettingsAction;

    private NewFolderActionButton newFolderAction;

    private ConsolidatedNewEntryAction newEntryAction;

    private DeleteEntryAction deleteEntryAction;

    private ConsulPeriodicStatusCheckController consulPeriodicStatusCheckController;

    public ConsulExplorer(Project project) {
        super(true);

        this.project = project;

        this.messageBus = this.project.getMessageBus();
        this.busConnection = messageBus.connect();

        this.consulConfigurationComboBoxAction = new ConsulConfigurationComboBoxAction(this.messageBus);
        this.consulConfigurationComboBoxAction.refreshItems();

        ConsulConfiguration consulConfiguration = consulConfigurationComboBoxAction.getSelection();
        this.consul = new Consul(consulConfiguration);
        initActions();

        setToolbar(createToolbarPanel());

        configureMessageBus();

        this.tree = new ConsulTree();
        initializeTreeModel();
        bindTreeModel();
        TreeUtil.installActions(tree);
        new TreeSpeedSearch(this.tree);
        this.treeExpander = new DefaultTreeExpander(this.tree);

        this.consulPeriodicStatusCheckController = new ConsulPeriodicStatusCheckController(this.tree);
        this.consulPeriodicStatusCheckController.restartPeriodicTreeStatusCheck(consulConfiguration);

        this.newFolderAction = new NewFolderActionButton(this.consul);
        this.newEntryAction = new ConsolidatedNewEntryAction(new NewEntryAction(this.consul), this.newFolderAction, tree, this.consul);
        this.deleteEntryAction = new DeleteEntryAction(this.consul);
        this.updateEntryAction = new UpdateEntryAction(this.consul);
        JPanel decoratedTree = ToolbarDecorator.createDecorator(this.tree)
                .disableUpDownActions()
                .setAddAction(this.newEntryAction)
                .setAddActionUpdater(this.newEntryAction)
                .setRemoveAction(this.deleteEntryAction)
                .setRemoveActionUpdater(this.deleteEntryAction)
                .addExtraAction(AnActionButton.fromAction(this.exportFolderAction))
                .addExtraAction(AnActionButton.fromAction(CommonActionsManager.getInstance()
                        .createExpandAllAction(this.treeExpander)))
                .addExtraAction(AnActionButton.fromAction(CommonActionsManager.getInstance()
                        .createCollapseAllAction(this.treeExpander)))
                .createPanel();


        this.keyAndValuePanel = new KeyAndValuePanel(this.messageBus, this.treeModel);

        JBSplitter splitter = new JBSplitter(true, 0.6f, 0.1f, 0.9f);
        splitter.setFirstComponent(decoratedTree);
        splitter.setSecondComponent(this.keyAndValuePanel);

        setContent(ScrollPaneFactory.createScrollPane(splitter));
    }

    private void bindTreeModel() {
        this.exportFolderAction.setTreeModel(this.treeModel);
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
                consulPeriodicStatusCheckController.restartPeriodicTreeStatusCheck(newConfiguration);
            }
        });
        this.busConnection.subscribe(Topics.KeyValueChanged.KEY_VALUE_CHANGED, new Topics.KeyValueChanged() {
            @Override
            public void keyValueChanged(KeyAndValue keyAndValue) {
                updateEntryAction.update(keyAndValue);
                // prevent seeing our most recent changes as the remote changes by someone else
                consulPeriodicStatusCheckController.clearLocalTree();
            }
        });
    }

    private void initActions() {
        this.refreshTreeAction = new RefreshTreeAction(this.messageBus);
        this.exportFolderAction = new ExportFolderAction(this.consul, this.messageBus);
        this.showSettingsAction = new ShowSettingsAction();
    }

    private JComponent createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(this.refreshTreeAction);
        group.add(this.consulConfigurationComboBoxAction);
        group.addSeparator();
        group.add(this.showSettingsAction);

        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("consulToolbar", group, true);
        actionToolBar.setTargetComponent(this.tree);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }

    private void initializeTreeModel() {
        treeModel = new ConsulTreeModel(tree, this.consul);
        treeModel.setOnValueSelectedListener(this::treeValueSelected);
        treeModel.setTreeModelLoadingListener(this);
        tree.setModel(treeModel);
        tree.addTreeWillExpandListener(this.treeModel);
        tree.addTreeSelectionListener(this.treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void treeValueSelected(KeyAndValue kv) {
        this.keyAndValuePanel.setKeyAndValue(kv);
        this.updateEntryAction.isEnabled(this.tree);

        this.messageBus.syncPublisher(Topics.ConsulTreeSelectionChanged.CONSUL_TREE_SELECTION_CHANGED)
                .consulTreeSelectionChanged(kv);
    }

    public void refresh() {
        this.selectionPath = this.tree.getSelectionPath();
        initializeTreeModel();
    }

    public void setKeyValuesVisible(boolean keyValuesVisible) {
        this.tree.setKeyValuesVisible(keyValuesVisible);
    }

    public boolean getKeyValuesVisible() {
        return this.tree.getKeyValuesVisible();
    }

    private TreePath selectionPath;

    @Override
    public void onBeforeTreeModelLoading() {
    }

    @Override
    public void onTreeModelSuccessfullyLoadedListener() {
        if (this.selectionPath != null) {
            TreeUtils.expandConsulTree(this.tree, TreeUtils.removeHead(this.selectionPath));
        }
    }
}
