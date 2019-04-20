package com.github.novotnyr.idea.consul.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "consul-hosts", storages = @Storage("consul.xml"))
public class PluginSettings implements PersistentStateComponent<PluginSettings.State> {
    public static PluginSettings getInstance() {
        return ServiceManager.getService(PluginSettings.class);
    }

    public static class State {
        public List<ConsulConfiguration> consulConfigurationList = new ArrayList<>();
    }

    private State state = new State();

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    public void setConsulConfigurationList(List<ConsulConfiguration> consulConfigurationList) {
        this.state.consulConfigurationList = consulConfigurationList;
    }

    public List<ConsulConfiguration> getConsulConfigurationList() {
        return this.state.consulConfigurationList;
    }
}
