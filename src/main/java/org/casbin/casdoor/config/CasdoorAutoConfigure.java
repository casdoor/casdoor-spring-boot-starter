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

    @Bean
    @ConditionalOnMissingBean
    AdapterService getCasdoorAdapterService(CasdoorConfiguration config) {
        return new AdapterService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationService getCasdoorApplicationService(CasdoorConfiguration config) {
        return new ApplicationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    CertService getCasdoorCertService(CasdoorConfiguration config) {
        return new CertService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    GroupService getCasdoorGroupService(CasdoorConfiguration config) {
        return new GroupService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    InvitationService getCasdoorInvitationService(CasdoorConfiguration config) {
        return new InvitationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    LdapService getCasdoorLdapService(CasdoorConfiguration config) {
        return new LdapService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    MfaService getCasdoorMfaService(CasdoorConfiguration config) {
        return new MfaService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    ModelService getCasdoorModelService(CasdoorConfiguration config) {
        return new ModelService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    NotificationService getCasdoorNotificationService(CasdoorConfiguration config) {
        return new NotificationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    OrderService getCasdoorOrderService(CasdoorConfiguration config) {
        return new OrderService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    OrganizationService getCasdoorOrganizationService(CasdoorConfiguration config) {
        return new OrganizationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    PaymentService getCasdoorPaymentService(CasdoorConfiguration config) {
        return new PaymentService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    PlanService getCasdoorPlanService(CasdoorConfiguration config) {
        return new PlanService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    PolicyService getCasdoorPolicyService(CasdoorConfiguration config) {
        return new PolicyService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    PricingService getCasdoorPricingService(CasdoorConfiguration config) {
        return new PricingService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    ProductService getCasdoorProductService(CasdoorConfiguration config) {
        return new ProductService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    ProviderService getCasdoorProviderService(CasdoorConfiguration config) {
        return new ProviderService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    RecordService getCasdoorRecordService(CasdoorConfiguration config) {
        return new RecordService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    SessionService getCasdoorSessionService(CasdoorConfiguration config) {
        return new SessionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    SubscriptionService getCasdoorSubscriptionService(CasdoorConfiguration config) {
        return new SubscriptionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    SyncerService getCasdoorSyncerService(CasdoorConfiguration config) {
        return new SyncerService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    TransactionService getCasdoorTransactionService(CasdoorConfiguration config) {
        return new TransactionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    WebhookService getCasdoorWebhookService(CasdoorConfiguration config) {
        return new WebhookService(config);
    }
}
