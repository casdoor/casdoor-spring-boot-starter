package org.casbin.casdoor.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tangyang9464
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "casdoor")
public class CasdoorConfiguration extends Config{}
