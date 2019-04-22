package com.github.novotnyr.idea.consul.scheduling;

import com.github.novotnyr.idea.consul.Topics;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.config.PluginSettings;
import com.github.novotnyr.idea.consul.tree.ConsulTree;
import com.google.common.collect.MapDifference;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.UIUtil;

public class ConsulPeriodicStatusCheckController {
    private final Logger logger = Logger.getInstance(ConsulPeriodicStatusCheckController.class);

    private final ConsulTree tree;

    private final PluginSettings pluginSettings = PluginSettings.getInstance();

    private ConsulPeriodicStatusChecker consulPeriodicStatusChecker;

    public ConsulPeriodicStatusCheckController(ConsulTree tree) {
        this.tree = tree;
        initMessageBus();
    }

    private void initMessageBus() {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.connect()
                .subscribe(Topics.PluginConfigurationChanged.PLUGIN_CONFIGURATION_CHANGED, new Topics.PluginConfigurationChanged() {
                    @Override
                    public void consulPluginConfigurationChanged() {
                        if (consulPeriodicStatusChecker == null) {
                            logger.debug("No Periodic Consul checker found");
                            return;
                        }
                        if (!isPeriodicCheckEnabled()) {
                            consulPeriodicStatusChecker.cancel();
                        } else {
                            logger.debug("Rescheduling Periodic Consul Checker");
                            consulPeriodicStatusChecker.rescheduleWithPeriod(getPeriodInSeconds());
                        }

                    }
                });
    }

    public void restartPeriodicTreeStatusCheck(ConsulConfiguration newConfiguration) {
        if (this.consulPeriodicStatusChecker != null) {
            this.consulPeriodicStatusChecker.cancel();
            this.consulPeriodicStatusChecker.removeRemoteTreeChangedListener();
            this.consulPeriodicStatusChecker = null;
        }

        if (!isPeriodicCheckEnabled()) {
            return;
        }

        this.consulPeriodicStatusChecker = new ConsulPeriodicStatusChecker(newConfiguration);
        this.consulPeriodicStatusChecker.setPeriodInSeconds(getPeriodInSeconds());
        this.consulPeriodicStatusChecker.setRemoteTreeChangedListener(new ConsulPeriodicStatusChecker.OnRemoteTreeChangedListener() {
            @Override
            public void onRemoteTreeChanged(MapDifference<String, String> difference) {
                UIUtil.invokeLaterIfNeeded(new Runnable() {
                    @Override
                    public void run() {
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder("Remote Consul tree has been changed. Please refresh!", MessageType.INFO, null)
                                .setFadeoutTime(7500)
                                .createBalloon()
                                .show(RelativePoint.getNorthWestOf(ConsulPeriodicStatusCheckController.this.tree),
                                        Balloon.Position.atRight);
                    }
                });
            }
        });
        this.consulPeriodicStatusChecker.schedule();
    }

    public void clearLocalTree() {
        if (this.consulPeriodicStatusChecker != null) {
            this.consulPeriodicStatusChecker.clearLocalTree();
        }
    }

    protected int getPeriodInSeconds() {
        return this.pluginSettings.getRemoteConsulTreeRefreshInterval();

    }

    protected boolean isPeriodicCheckEnabled() {
        return getPeriodInSeconds() >= 0;
    }
}
