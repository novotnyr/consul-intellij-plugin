package com.github.novotnyr.idea.consul.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "consul-hosts", storages = @Storage("consul.xml"))
public class ConsulConfigurationPersistentService implements PersistentStateComponent<ConsulConfigurationPersistentService.State> {
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

    protected ConsulConfiguration parse(String definition) {
        try {
            String[] split = definition.split("\\|");
            return new ConsulConfiguration(split[0], Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
