// Copyright 2026 The Casdoor Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.casdoor.config;

import org.casbin.casdoor.service.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Registers all the services of casdoor-java-sdk as beans, configured by the {@code casdoor.*} properties. Each bean
 * can be replaced by defining a bean of the same type.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "casdoor", name = "endpoint")
@EnableConfigurationProperties(CasdoorConfiguration.class)
public class CasdoorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AccountService casdoorAccountService(CasdoorConfiguration config) {
        return new AccountService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public AdapterService casdoorAdapterService(CasdoorConfiguration config) {
        return new AdapterService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationService casdoorApplicationService(CasdoorConfiguration config) {
        return new ApplicationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService casdoorAuthService(CasdoorConfiguration config) {
        return new AuthService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public CertService casdoorCertService(CasdoorConfiguration config) {
        return new CertService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailService casdoorEmailService(CasdoorConfiguration config) {
        return new EmailService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public EnforcerService casdoorEnforcerService(CasdoorConfiguration config) {
        return new EnforcerService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public GroupService casdoorGroupService(CasdoorConfiguration config) {
        return new GroupService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvitationService casdoorInvitationService(CasdoorConfiguration config) {
        return new InvitationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdapService casdoorLdapService(CasdoorConfiguration config) {
        return new LdapService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public MfaService casdoorMfaService(CasdoorConfiguration config) {
        return new MfaService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ModelService casdoorModelService(CasdoorConfiguration config) {
        return new ModelService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public NotificationService casdoorNotificationService(CasdoorConfiguration config) {
        return new NotificationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public OrderService casdoorOrderService(CasdoorConfiguration config) {
        return new OrderService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public OrganizationService casdoorOrganizationService(CasdoorConfiguration config) {
        return new OrganizationService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PaymentService casdoorPaymentService(CasdoorConfiguration config) {
        return new PaymentService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionService casdoorPermissionService(CasdoorConfiguration config) {
        return new PermissionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PlanService casdoorPlanService(CasdoorConfiguration config) {
        return new PlanService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PolicyService casdoorPolicyService(CasdoorConfiguration config) {
        return new PolicyService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PricingService casdoorPricingService(CasdoorConfiguration config) {
        return new PricingService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProductService casdoorProductService(CasdoorConfiguration config) {
        return new ProductService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderService casdoorProviderService(CasdoorConfiguration config) {
        return new ProviderService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordService casdoorRecordService(CasdoorConfiguration config) {
        return new RecordService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceService casdoorResourceService(CasdoorConfiguration config) {
        return new ResourceService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleService casdoorRoleService(CasdoorConfiguration config) {
        return new RoleService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionService casdoorSessionService(CasdoorConfiguration config) {
        return new SessionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsService casdoorSmsService(CasdoorConfiguration config) {
        return new SmsService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public SubscriptionService casdoorSubscriptionService(CasdoorConfiguration config) {
        return new SubscriptionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public SyncerService casdoorSyncerService(CasdoorConfiguration config) {
        return new SyncerService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenService casdoorTokenService(CasdoorConfiguration config) {
        return new TokenService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionService casdoorTransactionService(CasdoorConfiguration config) {
        return new TransactionService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserService casdoorUserService(CasdoorConfiguration config) {
        return new UserService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebhookService casdoorWebhookService(CasdoorConfiguration config) {
        return new WebhookService(config);
    }
}
