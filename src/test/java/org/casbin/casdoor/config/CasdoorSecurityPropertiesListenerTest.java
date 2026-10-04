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

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

class CasdoorSecurityPropertiesListenerTest {

    private static final String REGISTRATION = "spring.security.oauth2.client.registration.casdoor.";

    private static final String PROVIDER = "spring.security.oauth2.client.provider.casdoor.";

    private static final String RESOURCE_SERVER = "spring.security.oauth2.resourceserver.jwt.";

    private static StandardEnvironment configure(Map<String, Object> properties, ClassLoader classLoader) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
        CasdoorSecurityPropertiesListener.addProperties(environment, classLoader);
        return environment;
    }

    private static StandardEnvironment configure(Map<String, Object> properties) {
        return configure(properties, CasdoorSecurityPropertiesListenerTest.class.getClassLoader());
    }

    @Test
    void configuresOAuth2LoginAndResourceServer() {
        StandardEnvironment environment = configure(Map.of(
                "casdoor.endpoint", "https://door.example.com/",
                "casdoor.clientId", "client-id",
                "casdoor.client-secret", "client-secret"));

        assertThat(environment.getProperty(REGISTRATION + "client-id")).isEqualTo("client-id");
        assertThat(environment.getProperty(REGISTRATION + "client-secret")).isEqualTo("client-secret");
        assertThat(environment.getProperty(REGISTRATION + "provider")).isEqualTo("casdoor");
        assertThat(environment.getProperty(REGISTRATION + "authorization-grant-type")).isEqualTo("authorization_code");
        assertThat(environment.getProperty(REGISTRATION + "scope")).isEqualTo("openid,profile,email");
        assertThat(environment.getProperty(PROVIDER + "authorization-uri")).isEqualTo("https://door.example.com/login/oauth/authorize");
        assertThat(environment.getProperty(PROVIDER + "token-uri")).isEqualTo("https://door.example.com/api/login/oauth/access_token");
        assertThat(environment.getProperty(PROVIDER + "user-info-uri")).isEqualTo("https://door.example.com/api/userinfo");
        assertThat(environment.getProperty(PROVIDER + "jwk-set-uri")).isEqualTo("https://door.example.com/.well-known/jwks");
        assertThat(environment.getProperty(PROVIDER + "user-name-attribute")).isEqualTo("preferred_username");
        assertThat(environment.getProperty(RESOURCE_SERVER + "jwk-set-uri")).isEqualTo("https://door.example.com/.well-known/jwks");
        assertThat(environment.getProperty(RESOURCE_SERVER + "audiences")).isEqualTo("client-id");
    }

    @Test
    void propertiesOfTheApplicationWin() {
        StandardEnvironment environment = configure(Map.of(
                "casdoor.endpoint", "http://localhost:8000",
                "casdoor.client-id", "client-id",
                REGISTRATION + "scope", "openid",
                RESOURCE_SERVER + "audiences", "another-client-id"));

        assertThat(environment.getProperty(REGISTRATION + "scope")).isEqualTo("openid");
        assertThat(environment.getProperty(RESOURCE_SERVER + "audiences")).isEqualTo("another-client-id");
        assertThat(environment.getProperty(REGISTRATION + "client-id")).isEqualTo("client-id");
    }

    @Test
    void doesNothingWhenDisabled() {
        StandardEnvironment environment = configure(Map.of(
                "casdoor.endpoint", "http://localhost:8000",
                "casdoor.client-id", "client-id",
                "casdoor.security.enabled", "false"));

        assertThat(environment.getPropertySources().contains(CasdoorSecurityPropertiesListener.PROPERTY_SOURCE_NAME)).isFalse();
    }

    @Test
    void doesNothingWithoutClientId() {
        StandardEnvironment environment = configure(Map.of("casdoor.endpoint", "http://localhost:8000"));

        assertThat(environment.getPropertySources().contains(CasdoorSecurityPropertiesListener.PROPERTY_SOURCE_NAME)).isFalse();
    }

    @Test
    void onlyConfiguresWhatIsOnTheClasspath() {
        Map<String, Object> properties = Map.of("casdoor.endpoint", "http://localhost:8000", "casdoor.client-id", "client-id");

        StandardEnvironment resourceServerOnly = configure(properties, new FilteredClassLoader(ClientRegistration.class));
        assertThat(resourceServerOnly.containsProperty(REGISTRATION + "client-id")).isFalse();
        assertThat(resourceServerOnly.containsProperty(RESOURCE_SERVER + "jwk-set-uri")).isTrue();

        StandardEnvironment clientOnly = configure(properties, new FilteredClassLoader(BearerTokenAuthenticationToken.class));
        assertThat(clientOnly.containsProperty(REGISTRATION + "client-id")).isTrue();
        assertThat(clientOnly.containsProperty(RESOURCE_SERVER + "jwk-set-uri")).isFalse();
    }
}
