package org.casbin.casdoor.config;

import org.casbin.casdoor.service.*;
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
    CasdoorUserService getCasdoorUserService(CasdoorConfiguration config) {
        return new CasdoorUserService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorAuthService getCasdoorAuthService(CasdoorConfiguration config) {
        return new CasdoorAuthService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorSmsService getCasdoorSmsService(CasdoorConfiguration config) {
        return new CasdoorSmsService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorEmailService getCasdoorEmailService(CasdoorConfiguration config) {
        return new CasdoorEmailService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorResourceService getCasdoorResourceService(CasdoorConfiguration config) {
        return new CasdoorResourceService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorAccountService getCasdoorAccountService(CasdoorConfiguration config) {
        return new CasdoorAccountService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorEnforcerService getCasdoorEnforcerService(CasdoorConfiguration config) {
        return new CasdoorEnforcerService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorPermissionService getCasdoorPermissionService(CasdoorConfiguration config) {
        return new CasdoorPermissionService(config);
    }


    @Bean
    @ConditionalOnMissingBean
    CasdoorRoleService getCasdoorRoleService(CasdoorConfiguration config) {
        return new CasdoorRoleService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorTokenService getCasdoorTokenService(CasdoorConfiguration config) {
        return new CasdoorTokenService(config);
    }

}
