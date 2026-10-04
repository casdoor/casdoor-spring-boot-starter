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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Maps the Casdoor roles of the user to {@code ROLE_<name>} authorities, for OAuth2 login and for the JWT resource
 * server. The OAuth2 client and resource server themselves are configured by Spring Boot, from the properties added by
 * {@link CasdoorSecurityPropertiesListener}.
 */
@AutoConfiguration(after = CasdoorAutoConfiguration.class, afterName = {
        // a JwtAuthenticationConverter configured with the spring.security.oauth2.resourceserver.jwt.* properties wins
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
        "org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration"})
@ConditionalOnClass(GrantedAuthoritiesMapper.class)
@ConditionalOnBean(CasdoorConfiguration.class)
@ConditionalOnProperty(prefix = "casdoor.security", name = "enabled", matchIfMissing = true)
public class CasdoorSecurityAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.security.oauth2.client.registration.ClientRegistration")
    static class OAuth2LoginConfiguration {

        @Bean
        @ConditionalOnMissingBean
        GrantedAuthoritiesMapper casdoorGrantedAuthoritiesMapper(CasdoorConfiguration config) {
            return new CasdoorGrantedAuthoritiesMapper(config.getClientId());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ResourceServerConfiguration {

        @Bean
        @ConditionalOnMissingBean
        JwtAuthenticationConverter casdoorJwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                List<GrantedAuthority> authorities = new ArrayList<>(scopes.convert(jwt));
                authorities.addAll(CasdoorRoles.toAuthorities(jwt.getClaims().get(CasdoorRoles.CLAIM)));
                return authorities;
            });
            return converter;
        }
    }
}
