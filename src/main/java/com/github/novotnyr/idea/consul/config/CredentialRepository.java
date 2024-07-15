package com.github.novotnyr.idea.consul.config;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CredentialRepository {
    @Nullable
    public static CredentialRepository getInstance() {
        return ApplicationManager.getApplication().getService(CredentialRepository.class);
    }

    public ConsulCredentials find(PluginSettings.PersistedConsulConfiguration consulConfiguration) {
        ConsulCredentials credentials = new ConsulCredentials();

        extractSecureElement(consulConfiguration, "username", credentials::setUser);
        extractSecureElement(consulConfiguration, "password", credentials::setPassword);
        extractSecureElement(consulConfiguration, "acl", credentials::setAclToken);

        return credentials;
    }

    public void persist(ConsulConfiguration consulConfiguration) {
        PluginSettings.PersistedConsulConfiguration persistedConsulConfiguration
                = PluginSettings.PersistedConsulConfiguration.of(consulConfiguration);
        ConsulCredentials credentials = new ConsulCredentials();
        credentials.setUser(consulConfiguration.getUser());
        credentials.setPassword(consulConfiguration.getPassword());
        credentials.setAclToken(consulConfiguration.getAclToken());
        persist(persistedConsulConfiguration, credentials);
    }

    public void persist(PluginSettings.PersistedConsulConfiguration consulConfiguration, ConsulCredentials credentials) {
        persist(consulConfiguration,"username", credentials::getUser);
        persist(consulConfiguration,"acl", credentials::getAclToken);
        persist(consulConfiguration,"password", credentials::getPassword);
    }

    protected void persist(PluginSettings.PersistedConsulConfiguration consulConfiguration, String key, Supplier<String> provider) {
        String secureElement = provider.get();
        if (secureElement == null) {
            return;
        }
        CredentialAttributes attr = getCredentialAttributes(consulConfiguration, key);
        Credentials credentials = new Credentials(key, secureElement);
        getPasswordSafe().set(attr, credentials);
    }

    private void extractSecureElement(PluginSettings.PersistedConsulConfiguration consulConfiguration, String key, Consumer<String> assigner) {
        CredentialAttributes attr = getCredentialAttributes(consulConfiguration, key);
        Credentials credentials = getPasswordSafe().get(attr);
        if (credentials == null) {
            return;
        }
        String password = credentials.getPasswordAsString();
        if (password != null) {
            assigner.accept(password);
        }
    }

    private PasswordSafe getPasswordSafe() {
        return PasswordSafe.getInstance();
    }

    @NotNull
    private CredentialAttributes getCredentialAttributes(PluginSettings.PersistedConsulConfiguration consulConfiguration, String key) {
        return new CredentialAttributes(consulConfiguration.getAuthority() + "/" + key, key);
    }


}
