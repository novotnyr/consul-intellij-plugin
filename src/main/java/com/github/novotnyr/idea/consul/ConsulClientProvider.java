package com.github.novotnyr.idea.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class ConsulClientProvider {
    private ConsulConfiguration configuration;

    /**
     * Configure the provider to return the Consul clients
     * according to the specified configuration
     * @param configuration Consul configuration
     */
    public ConsulClientProvider(ConsulConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Produce a new instance of Consul client
     * @return Consul client instance
     */
    public ConsulClient getConsulClient() {
        return new ConsulClient(getConsulRawClient());
    }


    protected ConsulRawClient getConsulRawClient() {
        CredentialsProvider provider = getCredentialsProvider();
        var httpClient = HttpClients.custom()
                                    .setDefaultCredentialsProvider(provider)
                                    .setRedirectStrategy(new LaxRedirectStrategy())
                                    .build();
        return new ConsulRawClient(getHost(), this.configuration.getPort(), httpClient);
    }

    private String getHost() {
        if (this.configuration.isUsingTls()) {
            return "https://" + this.configuration.getHost();
        }
        return this.configuration.getHost();
    }

    private CredentialsProvider getCredentialsProvider() {
        if(this.configuration.getUser() == null) {
            return null;
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.configuration.getUser(), this.configuration.getPassword());
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }
}
