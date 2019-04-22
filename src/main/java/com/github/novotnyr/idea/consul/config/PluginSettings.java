package com.github.novotnyr.idea.consul.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@State(name = "consul-hosts", storages = @Storage("consul.xml"))
public class PluginSettings implements PersistentStateComponent<PluginSettings> {

    public static final int NO_REFRESH = -1;

    public static PluginSettings getInstance() {
        return ServiceManager.getService(PluginSettings.class);
    }

    private List<PersistedConsulConfiguration> consulConfigurationList = new ArrayList<>();

    private int remoteConsulTreeRefreshInterval = NO_REFRESH;

    @Nullable
    @Override
    public PluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(PluginSettings persistedSettings) {
        List<PersistedConsulConfiguration> persistedConfigurations = persistedSettings.getConsulConfigurationList();
        if (persistedConfigurations == null || persistedConfigurations.isEmpty()) {
            return;
        }
        List<PersistedConsulConfiguration> filteredPersistedConfigurations = persistedConfigurations.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (filteredPersistedConfigurations.isEmpty()) {
            return;
        }
        setConsulConfigurationList(filteredPersistedConfigurations);
        this.remoteConsulTreeRefreshInterval = persistedSettings.getRemoteConsulTreeRefreshInterval();
    }

    public void setConsulConfigurationList(List<PersistedConsulConfiguration> consulConfigurationList) {
        this.consulConfigurationList = consulConfigurationList;
    }

    public List<PersistedConsulConfiguration> getConsulConfigurationList() {
        return this.consulConfigurationList;
    }

    @Transient
    public void setFullConsulConfigurations(List<ConsulConfiguration> fullConsulConfigurations) {
        List<PersistedConsulConfiguration> persistedConfigurations = fullConsulConfigurations.stream()
                .map(this::persist)
                .map(PersistedConsulConfiguration::of)
                .collect(Collectors.toList());
        setConsulConfigurationList(persistedConfigurations);
    }

    private ConsulConfiguration persist(ConsulConfiguration consulConfiguration) {
        CredentialRepository credentialRepository = ServiceManager.getService(CredentialRepository.class);
        credentialRepository.persist(consulConfiguration);
        return consulConfiguration;
    }

    @Transient
    public List<ConsulConfiguration> getFullConsulConfigurations() {
        List<ConsulConfiguration> configList = new ArrayList<>();
        ConsulConfigurationRepository consulConfigurationRepository = ServiceManager.getService(ConsulConfigurationRepository.class);
        for (PersistedConsulConfiguration persistedConfig : getConsulConfigurationList()) {
            ConsulConfiguration fullConfig
                    = consulConfigurationRepository.fromPluginSettings(persistedConfig);
            configList.add(fullConfig);
        }
        return configList;
    }

    public void setRemoteConsulTreeRefreshInterval(int remoteConsulTreeRefreshInterval) {
        this.remoteConsulTreeRefreshInterval = remoteConsulTreeRefreshInterval;
    }

    public int getRemoteConsulTreeRefreshInterval() {
        return remoteConsulTreeRefreshInterval;
    }

    /**
     * A compatibility class that does not carry credentials (HTTP/ACL)
     * due to IntelliJ API change
     */
    public static class PersistedConsulConfiguration {
        public static final String DEFAULT_CONSUL_HOST = "localhost";

        public static final int DEFAULT_CONSUL_PORT = 8500;

        public static final String DEFAULT_TOKEN = null;

        private String host = DEFAULT_CONSUL_HOST;

        private int port = DEFAULT_CONSUL_PORT;

        private String datacenter;

        private boolean usingTls;

        public static PersistedConsulConfiguration of(ConsulConfiguration fullConfiguration) {
            PersistedConsulConfiguration cfg = new PersistedConsulConfiguration();
            cfg.setHost(fullConfiguration.getHost());
            cfg.setPort(fullConfiguration.getPort());
            cfg.setDatacenter(fullConfiguration.getDatacenter());
            cfg.setUsingTls(fullConfiguration.isUsingTls());
            return cfg;
        }

        public String getAuthority() {
            return this.host + ":" + this.port;
        }

        public String toUrl() {
            StringBuilder url = new StringBuilder();
            if (isUsingTls()) {
                url.append("https://");
            } else {
                url.append("http://");
            }
            url.append(host).append(":").append(port);
            if (datacenter != null) {
                url.append("?dc=").append(datacenter);
            }
            return url.toString();
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDatacenter() {
            return datacenter;
        }

        public void setDatacenter(String datacenter) {
            this.datacenter = datacenter;
        }

        public boolean isUsingTls() {
            return usingTls;
        }

        public void setUsingTls(boolean usingTls) {
            this.usingTls = usingTls;
        }
    }
}
