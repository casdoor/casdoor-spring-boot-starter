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
    UserService getUserService(CasdoorConfiguration config) {
        return new UserService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    AuthService getAuthService(CasdoorConfiguration config) {
        return new AuthService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    SmsService getCasdoorSmsService(CasdoorConfiguration config) {
        return new SmsService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    EmailService getCasdoorEmailService(CasdoorConfiguration config) {
        return new EmailService(config);
    }

    @Bean
    @ConditionalOnMissingBean
   ResourceService getCasdoorResourceService(CasdoorConfiguration config) {
        return new ResourceService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    AccountService getCasdoorAccountService(CasdoorConfiguration config) {
        return new AccountService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    EnforcerService getCasdoorEnforcerService(CasdoorConfiguration config) {
        return new EnforcerService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    PermissionService getCasdoorPermissionService(CasdoorConfiguration config) {
        return new PermissionService(config);
    }


    @Bean
    @ConditionalOnMissingBean
    RoleService getCasdoorRoleService(CasdoorConfiguration config) {
        return new RoleService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    TokenService getCasdoorTokenService(CasdoorConfiguration config) {
        return new TokenService(config);
    }
}
