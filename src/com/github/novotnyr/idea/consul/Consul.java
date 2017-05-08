package com.github.novotnyr.idea.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;

import java.util.List;

public class Consul {

    public static final PutParams EMPTY_PUT_PARAMS = null;

    private ConsulClientProvider consulClientProvider;

    private ConsulConfiguration configuration;

    public Consul() {
        // empty constructor
    }

    public Consul(ConsulConfiguration configuration) {
        setConfiguration(configuration);
    }

    public List<GetValue> getAllValues() {
        Response<List<GetValue>> kvValues = getConsulClient().getKVValues("", configuration.getAclToken(), getQueryParams());
        return kvValues.getValue();
    }

    public void insert(String fqnKey, String value) {
        getConsulClient().setKVValue(fqnKey, value, configuration.getAclToken(), EMPTY_PUT_PARAMS, getQueryParams());
    }

    public void mkdir(String path) {
        String folderName = path;
        if (!path.endsWith("/")) {
            // by convention, keys ending with / denote a folder
            folderName = path + "/";
        }
        getConsulClient().setKVValue(folderName, "", configuration.getAclToken(), EMPTY_PUT_PARAMS, getQueryParams());
    }

    public void delete(String fullyQualifiedKey) {
        if(fullyQualifiedKey.endsWith("/")) {
            getConsulClient().deleteKVValues(fullyQualifiedKey, configuration.getAclToken(), getQueryParams());
        } else {
            getConsulClient().deleteKVValue(fullyQualifiedKey, configuration.getAclToken(), getQueryParams());
        }
    }

    public void update(String fullyQualifiedKey, String value) {
        getConsulClient().setKVValue(fullyQualifiedKey, value, configuration.getAclToken(), EMPTY_PUT_PARAMS, getQueryParams());
    }

    public void setConfiguration(ConsulConfiguration configuration) {
        this.consulClientProvider = new ConsulClientProvider(configuration);
        this.configuration = configuration;
    }

    protected QueryParams getQueryParams() {
        return QueryParams.Builder
                .builder()
                .setDatacenter(this.configuration.getDatacenter())
                .build();
    }

    protected ConsulClient getConsulClient() {
        if(this.consulClientProvider == null) {
            throw new ConsulException("Consul client must be set");
        }
        return this.consulClientProvider.getConsulClient();
    }

    public ConsulConfiguration getConfiguration() {
        return configuration;
    }
}
