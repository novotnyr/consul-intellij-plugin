package com.github.novotnyr.idea.consul.config;

import org.jetbrains.annotations.Nullable;

public class ConsulConfigurationRepository {
    private final CredentialRepository credentialRepository;

    public ConsulConfigurationRepository(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

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

        ConsulCredentials credentials = credentialRepository.find(persistedConfiguration);
        credentials.user().ifPresent(configuration::setUser);
        credentials.password().ifPresent(configuration::setPassword);
        credentials.aclToken().ifPresent(configuration::setAclToken);

        return configuration;
    }
}
