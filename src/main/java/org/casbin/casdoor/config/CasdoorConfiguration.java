package org.casbin.casdoor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tangyang9464
 */
@ConfigurationProperties(prefix = "casdoor")
public class CasdoorConfiguration {
    private String endpoint;

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
