package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.action.ConsulConfigurationComboBoxAction;
import com.github.novotnyr.idea.consul.action.DeleteEntryAction;
import com.github.novotnyr.idea.consul.action.ExportFolderAction;
import com.github.novotnyr.idea.consul.action.NewEntryAction;
import com.github.novotnyr.idea.consul.action.NewFolderAction;
import com.github.novotnyr.idea.consul.action.RefreshTreeAction;
import com.github.novotnyr.idea.consul.action.ShowSettingsAction;
import com.github.novotnyr.idea.consul.action.UpdateEntryAction;
import com.github.novotnyr.idea.consul.action2.ConsolidatedNewEntryAction;
import com.github.novotnyr.idea.consul.action2.DeleteEntryAction2;
import com.github.novotnyr.idea.consul.action2.NewEntryAction2;
import com.github.novotnyr.idea.consul.action2.NewFolderActionButton2;
import com.github.novotnyr.idea.consul.action2.UpdateEntryAction2;
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
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

    private UpdateEntryAction2 updateEntryAction2;

    private ConsulConfigurationComboBoxAction consulConfigurationComboBoxAction;

    private final MessageBus messageBus;

    private final MessageBusConnection busConnection;

    private ExportFolderAction exportFolderAction;

    private ConsulTree tree;

    private ConsulTreeModel treeModel;

    private KeyAndValuePanel keyAndValuePanel;

    private KeyAndValue selectedKeyAndValue;

    private RefreshTreeAction refreshTreeAction;

    private ShowSettingsAction showSettingsAction;

    public ConsulExplorer(Project project) {
        super(true);

        this.project = project;

        this.messageBus = this.project.getMessageBus();
        this.busConnection = messageBus.connect();

        this.consulConfigurationComboBoxAction = new ConsulConfigurationComboBoxAction(this.messageBus);
        this.consulConfigurationComboBoxAction.refreshItems();

        this.consul = new Consul(consulConfigurationComboBoxAction.getSelection());
        initActions();

        setToolbar(createToolbarPanel());

        bindTreeModel();
        configureMessageBus();

        this.tree = new ConsulTree();
        initializeTreeModel();
        TreeUtil.installActions(tree);
        installPopupHandler(tree);
        // installKeyboardPopupHandler(tree);


        NewFolderActionButton2 newFolderAction = new NewFolderActionButton2(this.consul);
        ConsolidatedNewEntryAction newEntryAction = new ConsolidatedNewEntryAction(new NewEntryAction2(this.consul), newFolderAction, tree, this.consul);
        DeleteEntryAction2 deleteEntryAction = new DeleteEntryAction2(this.consul);
        this.updateEntryAction2 = new UpdateEntryAction2(this.consul);
        JPanel decoratedTree = ToolbarDecorator.createDecorator(this.tree)
                .disableUpDownActions()
                .setAddAction(newEntryAction)
                .setAddActionUpdater(newEntryAction)
                .setRemoveAction(deleteEntryAction)
                .setRemoveActionUpdater(deleteEntryAction)
                .createPanel();


        this.keyAndValuePanel = new KeyAndValuePanel(this.messageBus, treeModel);


        JBSplitter splitter = new JBSplitter(true, 0.8f, 0.1f, 0.9f);
        splitter.setFirstComponent(decoratedTree);
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
                updateEntryAction2.update(keyAndValue);
            }
        });
    }

    private void initActions() {
        this.refreshTreeAction = new RefreshTreeAction(this.messageBus);
        this.newFolderAction = new NewFolderAction(this.consul, this.messageBus);
        this.newEntryAction = new NewEntryAction(this.consul, this.messageBus);
        this.deleteEntryAction = new DeleteEntryAction(this.consul, this.messageBus);
        this.updateEntryAction = new UpdateEntryAction(this.consul, this.messageBus);
        this.exportFolderAction = new ExportFolderAction(this.consul, this.messageBus);
        this.showSettingsAction = new ShowSettingsAction();
    }

    private JComponent createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(this.refreshTreeAction);
        group.add(this.newFolderAction);
        group.add(this.newEntryAction);
        group.add(this.deleteEntryAction);
        group.add(this.exportFolderAction);
        group.add(this.consulConfigurationComboBoxAction);

        group.addSeparator();

        group.add(this.showSettingsAction);

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
        this.updateEntryAction2.isEnabled(this.tree);
    }

    public void refresh() {
        initializeTreeModel();
    }

    @Nullable
    @Override
    public Object getData(String s) {
        /*
        if(PlatformDataKeys.SELECTED_ITEM.is(s)) {
            return this.selectedKeyAndValue;
        } else {
            if(PlatformDataKeys.CONTEXT_MENU_POINT.is(s)) {
                System.out.println("Context menu point " + s);
            } else if(LangDataKeys.IDE_VIEW.is(s)) {
                System.out.println("IDE View " + s);
            } else if(PlatformDataKeys.NAVIGATABLE_ARRAY.is(s)) {
                System.out.println("Navigatable array " + s);
            } else {
                System.out.println("Unknown data " + s);
            }
        }
        */
        return null;
    }

}
