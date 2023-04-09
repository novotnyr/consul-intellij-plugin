package com.github.novotnyr.idea.consul.config;

import org.jetbrains.annotations.Nullable;

public class ConsulConfigurationRepository {
    @Nullable
    public ConsulConfiguration fromPluginSettings(@Nullable PluginSettings.PersistedConsulConfiguration persistedConfiguration) {
        if (persistedConfiguration == null) {
            return null;
        }

        ConsulConfiguration configuration = new ConsulConfiguration();
        configuration.setHost(persistedConfiguration.getHost());
        configuration.setPort(persistedConfiguration.getPort());
        configuration.setDatacenter(persistedConfiguration.getDatacenter());
        configuration.setUsingTls(persistedConfiguration.isUsingTls());

        ConsulCredentials credentials = getCredentialsRepository().find(persistedConfiguration);
        credentials.user().ifPresent(configuration::setUser);
        credentials.password().ifPresent(configuration::setPassword);
        credentials.aclToken().ifPresent(configuration::setAclToken);

        return configuration;
    }

    private CredentialRepository getCredentialsRepository() {
        return CredentialRepository.getInstance();
    }
}
