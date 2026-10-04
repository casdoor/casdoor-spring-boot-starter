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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Turns the {@code casdoor.*} properties into the {@code spring.security.oauth2.*} properties of Spring Boot, so that
 * Spring Boot configures OAuth2 login (registration ID {@code casdoor}) and the JWT resource server to use Casdoor.
 * <p>
 * The properties are added with the lowest precedence: any {@code spring.security.oauth2.*} property set by the
 * application wins. A listener is used instead of an {@code EnvironmentPostProcessor}, because that interface moved to
 * another package in Spring Boot 4.
 */
public class CasdoorSecurityPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    static final String PROPERTY_SOURCE_NAME = "casdoorSecurity";

    static final String REGISTRATION_ID = "casdoor";

    private static final String CLIENT_REGISTRATION_CLASS =
            "org.springframework.security.oauth2.client.registration.ClientRegistration";

    private static final String BEARER_TOKEN_CLASS =
            "org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken";

    private static final String JWT_DECODER_CLASS = "org.springframework.security.oauth2.jwt.JwtDecoder";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ClassLoader classLoader = event.getSpringApplication().getClassLoader();
        addProperties(event.getEnvironment(), classLoader);
    }

    /**
     * Runs after Spring Boot has loaded the configuration files.
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    static void addProperties(ConfigurableEnvironment environment, ClassLoader classLoader) {
        Binder binder = Binder.get(environment);
        if (!binder.bind("casdoor.security.enabled", Boolean.class).orElse(true)) {
            return;
        }
        String endpoint = binder.bind("casdoor.endpoint", String.class).orElse("");
        String clientId = binder.bind("casdoor.client-id", String.class).orElse("");
        String clientSecret = binder.bind("casdoor.client-secret", String.class).orElse("");
        if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(clientId)) {
            return;
        }
        endpoint = endpoint.replaceAll("/+$", "");
        String jwkSetUri = endpoint + "/.well-known/jwks";

        Map<String, Object> properties = new LinkedHashMap<>();
        if (ClassUtils.isPresent(CLIENT_REGISTRATION_CLASS, classLoader)) {
            String registration = "spring.security.oauth2.client.registration." + REGISTRATION_ID + ".";
            properties.put(registration + "provider", REGISTRATION_ID);
            properties.put(registration + "client-id", clientId);
            properties.put(registration + "client-secret", clientSecret);
            properties.put(registration + "client-name", "Casdoor");
            properties.put(registration + "authorization-grant-type", "authorization_code");
            properties.put(registration + "redirect-uri", "{baseUrl}/login/oauth2/code/{registrationId}");
            properties.put(registration + "scope", "openid,profile,email");

            String provider = "spring.security.oauth2.client.provider." + REGISTRATION_ID + ".";
            properties.put(provider + "authorization-uri", endpoint + "/login/oauth/authorize");
            properties.put(provider + "token-uri", endpoint + "/api/login/oauth/access_token");
            properties.put(provider + "user-info-uri", endpoint + "/api/userinfo");
            properties.put(provider + "jwk-set-uri", jwkSetUri);
            properties.put(provider + "user-name-attribute", "preferred_username");
        }
        if (ClassUtils.isPresent(BEARER_TOKEN_CLASS, classLoader) && ClassUtils.isPresent(JWT_DECODER_CLASS, classLoader)) {
            properties.put("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", jwkSetUri);
            properties.put("spring.security.oauth2.resourceserver.jwt.audiences", clientId);
        }
        if (!properties.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }
}
