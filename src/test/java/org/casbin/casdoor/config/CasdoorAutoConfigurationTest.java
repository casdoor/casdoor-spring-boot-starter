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

import java.util.ArrayList;
import java.util.List;

import org.casbin.casdoor.service.AuthService;
import org.casbin.casdoor.service.Service;
import org.casbin.casdoor.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import static org.assertj.core.api.Assertions.assertThat;

class CasdoorAutoConfigurationTest {

    private static final AutoConfigurations CONFIGURATIONS =
            AutoConfigurations.of(CasdoorAutoConfiguration.class, CasdoorSecurityAutoConfiguration.class);

    private static final String[] PROPERTIES = {
            "casdoor.endpoint=http://localhost:8000",
            "casdoor.client-id=client-id",
            "casdoor.client-secret=client-secret"};

    private final ApplicationContextRunner runner = new ApplicationContextRunner().withConfiguration(CONFIGURATIONS);

    private final WebApplicationContextRunner webRunner = new WebApplicationContextRunner().withConfiguration(CONFIGURATIONS);

    @Test
    void doesNothingWithoutEndpoint() {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(CasdoorConfiguration.class);
            assertThat(context).doesNotHaveBean(UserService.class);
            assertThat(context).doesNotHaveBean(GrantedAuthoritiesMapper.class);
        });
    }

    @Test
    void bindsProperties() {
        runner.withPropertyValues(
                "casdoor.endpoint=http://localhost:8000",
                "casdoor.client-id=client-id",
                "casdoor.clientSecret=client-secret",
                "casdoor.certificate=certificate",
                "casdoor.organization-name=organization",
                "casdoor.application-name=application",
                "casdoor.custom-headers.Accept-Language=de",
                "casdoor.security.enabled=false").run(context -> {
            CasdoorConfiguration config = context.getBean(CasdoorConfiguration.class);
            assertThat(config.getEndpoint()).isEqualTo("http://localhost:8000");
            assertThat(config.getClientId()).isEqualTo("client-id");
            assertThat(config.getClientSecret()).isEqualTo("client-secret");
            assertThat(config.getCertificate()).isEqualTo("certificate");
            assertThat(config.getOrganizationName()).isEqualTo("organization");
            assertThat(config.getApplicationName()).isEqualTo("application");
            assertThat(config.getCustomHeaders()).containsEntry("Accept-Language", "de");
            assertThat(config.getSecurity().isEnabled()).isFalse();
        });
    }

    @Test
    void failsWithoutClientIdOrSecret() {
        runner.withPropertyValues("casdoor.endpoint=http://localhost:8000", "casdoor.client-id=client-id").run(context ->
                assertThat(context).getFailure().rootCause().hasMessage("casdoor.client-secret must be set"));
    }

    @Test
    void registersEveryServiceOfTheSdk() throws Exception {
        List<Class<?>> services = new ArrayList<>();
        for (Resource resource : new PathMatchingResourcePatternResolver()
                .getResources("classpath*:org/casbin/casdoor/service/*Service.class")) {
            String name = resource.getFilename().replace(".class", "");
            Class<?> service = Class.forName("org.casbin.casdoor.service." + name);
            if (service != Service.class) {
                services.add(service);
            }
        }
        assertThat(services).hasSizeGreaterThan(30);

        runner.withPropertyValues(PROPERTIES).run(context -> {
            for (Class<?> service : services) {
                assertThat(context).hasSingleBean(service);
            }
        });
    }

    @Test
    void backsOffWhenTheApplicationDefinesAService() {
        runner.withPropertyValues(PROPERTIES)
                .withUserConfiguration(CustomAuthServiceConfiguration.class)
                .run(context -> assertThat(context.getBean(AuthService.class))
                        .isSameAs(context.getBean("customAuthService")));
    }

    @Test
    void mapsRolesForSpringSecurity() {
        webRunner.withPropertyValues(PROPERTIES).run(context -> {
            assertThat(context).hasSingleBean(CasdoorGrantedAuthoritiesMapper.class);
            assertThat(context).hasSingleBean(JwtAuthenticationConverter.class);
        });
    }

    @Test
    void doesNotConfigureSpringSecurityWhenDisabled() {
        webRunner.withPropertyValues(PROPERTIES).withPropertyValues("casdoor.security.enabled=false").run(context -> {
            assertThat(context).hasSingleBean(UserService.class);
            assertThat(context).doesNotHaveBean(GrantedAuthoritiesMapper.class);
            assertThat(context).doesNotHaveBean(JwtAuthenticationConverter.class);
        });
    }

    @Test
    void doesNotConfigureTheResourceServerOutsideServletApplications() {
        runner.withPropertyValues(PROPERTIES).run(context -> {
            assertThat(context).hasSingleBean(GrantedAuthoritiesMapper.class);
            assertThat(context).doesNotHaveBean(JwtAuthenticationConverter.class);
        });
    }

    @Test
    void worksWithoutOAuth2Client() {
        webRunner.withPropertyValues(PROPERTIES)
                .withClassLoader(new FilteredClassLoader(ClientRegistration.class)).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(GrantedAuthoritiesMapper.class);
            assertThat(context).hasSingleBean(JwtAuthenticationConverter.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomAuthServiceConfiguration {

        @Bean
        AuthService customAuthService() {
            return new AuthService(new Config("http://localhost:8000", "custom-client-id", "custom-client-secret", "", "", ""));
        }
    }
}
