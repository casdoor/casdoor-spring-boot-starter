package org.casbin.casdoor.config;

import org.casbin.casdoor.Casdoor;
import org.casbin.casdoor.CasdoorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangyang9464
 */
@Configuration
@EnableConfigurationProperties(CasdoorConfiguration.class)
public class CasdoorAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "casdoor.endpoint")
    Casdoor starterService (CasdoorConfiguration properties) throws CasdoorException {
        return new Casdoor(properties.getEndpoint());
    }
}
