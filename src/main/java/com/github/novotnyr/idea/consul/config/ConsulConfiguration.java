package com.github.novotnyr.idea.consul.config;

import java.util.Objects;

/**
 * A configuration object for the Consul instance.
 */
public class ConsulConfiguration {
    public static final String DEFAULT_CONSUL_HOST = "localhost";

    public static final int DEFAULT_CONSUL_PORT = 8500;

    public static final String DEFAULT_TOKEN = null;

    private String host = DEFAULT_CONSUL_HOST;

    private int port = DEFAULT_CONSUL_PORT;

    private String aclToken = DEFAULT_TOKEN;

    private String user;

    private String password;

    private String datacenter;

    private boolean usingTls;

    /**
     * Create a configuration with default values.
     * <p>
     *     Uses the default consul host,
     *     the default port,
     *     the default configuration prefix,
     *     and no user, password, datacenter nor ACL token.
     * </p>
     */
    public ConsulConfiguration() {
        // empty constructor
    }

    /**
     * Create a configuration that points to the specific
     * host and port
     * @param host a Consul host
     * @param port a consul port
     */
    public ConsulConfiguration(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Return the host used by this configuration
     * @return the configuration host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the hostname of the Consul agent that will
     * be used.
     * @param host the Consul hostname
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Return the port of the Consul agent that will
     * be used.
     * @return
     */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAclToken() {
        return aclToken;
    }

    /**
     * Set the ACL token that will be sent to Consul along with requests.
     * The token corresponds to the <code>X-Consul-Token</code> request header or the
     * <code>token</code> querystring parameter.
     * @param aclToken ACL token to send with requests
     */
    public void setAclToken(String aclToken) {
        this.aclToken = aclToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsulConfiguration)) return false;
        ConsulConfiguration that = (ConsulConfiguration) o;
        return getPort() == that.getPort() &&
                isUsingTls() == that.isUsingTls() &&
                Objects.equals(getHost(), that.getHost()) &&
                Objects.equals(getAclToken(), that.getAclToken()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getDatacenter(), that.getDatacenter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort(), getAclToken(), getUser(), getPassword(), getDatacenter(), isUsingTls());
    }

    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        if (this.host != null) {
            sb.append(this.host).append(":").append(this.port);
        }
        if(sb.length() > 0) {
            return sb.toString();
        }
        return "Empty Consul configuration";
    }
}
