package com.github.novotnyr.idea.consul.config;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsulConfiguration)) return false;

        ConsulConfiguration that = (ConsulConfiguration) o;

        if (getPort() != that.getPort()) return false;
        if (getHost() != null ? !getHost().equals(that.getHost()) : that.getHost() != null) return false;
        if (getAclToken() != null ? !getAclToken().equals(that.getAclToken()) : that.getAclToken() != null)
            return false;
        if (getUser() != null ? !getUser().equals(that.getUser()) : that.getUser() != null) return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return getDatacenter() != null ? getDatacenter().equals(that.getDatacenter()) : that.getDatacenter() == null;
    }

    @Override
    public int hashCode() {
        int result = getHost() != null ? getHost().hashCode() : 0;
        result = 31 * result + getPort();
        result = 31 * result + (getAclToken() != null ? getAclToken().hashCode() : 0);
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getDatacenter() != null ? getDatacenter().hashCode() : 0);
        return result;
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
