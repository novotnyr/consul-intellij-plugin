package com.github.novotnyr.idea.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringTokenizer;

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
        setKVValue(fqnKey, value);
    }

    public void mkdir(String path) {
        String folderName = path;
        if (!path.endsWith("/")) {
            // by convention, keys ending with / denote a folder
            folderName = path + "/";
        }
        setKVValue(folderName, "");
    }

    public void delete(String fullyQualifiedKey) {
        if(fullyQualifiedKey.endsWith("/")) {
            getConsulClient().deleteKVValues(sanitizeKey(fullyQualifiedKey), configuration.getAclToken(), getQueryParams());
        } else {
            getConsulClient().deleteKVValue(sanitizeKey(fullyQualifiedKey), configuration.getAclToken(), getQueryParams());
        }
    }

    public void update(String fullyQualifiedKey, String value) {
        setKVValue(fullyQualifiedKey, value);
    }

    protected void setKVValue(String fullyQualifiedKey, String value) {
        getConsulClient().setKVValue(sanitizeKey(fullyQualifiedKey), value, configuration.getAclToken(), EMPTY_PUT_PARAMS, getQueryParams());
    }

    protected String sanitizeKey(String key) {
        StringBuilder result = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(key, "/");
        while (tokenizer.hasMoreElements()) {
            String urlEncodedToken = urlEncode(tokenizer.nextToken());
            if (result.length() == 0) {
                result.append(urlEncodedToken);
            } else {
                result.append("/").append(urlEncodedToken);
            }
        }
        if (endsWithSlash(key) && !endsWithSlash(result)) {
            result.append('/');
        }
        return result.toString();
    }

    private boolean endsWithSlash(CharSequence sequence) {
        if (sequence.length() == 0) {
            return false;
        }
        char suffix = sequence.charAt(sequence.length() - 1);
        return suffix == '/';
    }

    protected String urlEncode(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8);
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
