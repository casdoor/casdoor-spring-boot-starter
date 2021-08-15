package org.casbin.casdoor.config;

import org.casbin.casdoor.service.CasdoorAuthService;
import org.casbin.casdoor.service.CasdoorUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    CasdoorUserService getCasdoorUserService (CasdoorConfiguration config) {
        return new CasdoorUserService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorAuthService getCasdoorAuthService (CasdoorConfiguration config) {
        return new CasdoorAuthService(config);
    }
}
