package com.github.novotnyr.idea.consul.scheduling;

import com.ecwid.consul.v1.kv.model.GetValue;
import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.concurrency.AppExecutorUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ConsulPeriodicStatusChecker {
    private final Logger logger = Logger.getInstance(ConsulPeriodicStatusChecker.class);

    public static final int DEFAULT_PERIOD_IN_SECONDS = 60;

    private final ScheduledExecutorService scheduler = AppExecutorUtil.getAppScheduledExecutorService();

    private final ConsulConfiguration consulConfiguration;

    private AtomicReference<Map<String, String>> localTreeRef = new AtomicReference<>();

    private ScheduledFuture<?> periodicCheckFuture;

    private OnRemoteTreeChangedListener remoteTreeChangedListener = OnRemoteTreeChangedListener.EMPTY;

    private int periodInSeconds = DEFAULT_PERIOD_IN_SECONDS;

    public ConsulPeriodicStatusChecker(ConsulConfiguration consulConfiguration) {
        this.consulConfiguration = consulConfiguration;
    }

    public void schedule() {
        this.periodicCheckFuture = this.scheduler.scheduleWithFixedDelay(this::checkConsul, 5, this.periodInSeconds, TimeUnit.SECONDS);
    }

    public void rescheduleWithPeriod(int periodInSeconds) {
        logger.debug("Changing period for Consul checks to " + periodInSeconds + "s  for " + this.consulConfiguration.toSimpleString());
        if (isScheduled()) {
            cancel();
        }
        this.periodInSeconds = periodInSeconds;
        schedule();
    }


    public void checkConsul() {
        try {
            logger.debug("Retrieving remote Consul tree for " + this.consulConfiguration.toSimpleString());
            Consul consul = new Consul(this.consulConfiguration);
            List<GetValue> allValues = consul.getAllValues();
            if (allValues == null) {
                allValues = Collections.emptyList();
            }
            Map<String, String> remoteTree = new LinkedHashMap<>(allValues.size());
            for (GetValue getValue : allValues) {
                remoteTree.put(getValue.getKey(), getValue.getValue());
            }

            Map<String, String> localTree = this.localTreeRef.get();
            if (localTree == null) {
                this.localTreeRef.set(remoteTree);
            } else {
                MapDifference<String, String> difference = Maps.difference(localTree, remoteTree);
                this.localTreeRef.set(remoteTree);
                if (difference.entriesDiffering().isEmpty()) {
                    // no differences
                    return;
                }
                this.remoteTreeChangedListener.onRemoteTreeChanged(difference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRemoteTreeChangedListener(OnRemoteTreeChangedListener remoteTreeChangedListener) {
        this.remoteTreeChangedListener = remoteTreeChangedListener;
    }

    public void removeRemoteTreeChangedListener() {
        this.remoteTreeChangedListener = null;
    }

    public boolean isScheduled() {
        return this.periodicCheckFuture != null && !this.periodicCheckFuture.isDone();
    }

    public void cancel() {
        if (isScheduled()) {
            this.periodicCheckFuture.cancel(false);
            logger.debug("Periodic Consul check cancelled " + this.consulConfiguration.toSimpleString());
        }
    }

    public void clearLocalTree() {
        this.localTreeRef.set(null);
    }

    public void setPeriodInSeconds(int periodInSeconds) {
        this.periodInSeconds = periodInSeconds;
    }

    @FunctionalInterface
    public interface OnRemoteTreeChangedListener {
        void onRemoteTreeChanged(MapDifference<String, String> difference);

        OnRemoteTreeChangedListener EMPTY = difference -> {};
    }
}
