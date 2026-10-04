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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

/**
 * The {@code casdoor.*} properties. It is the {@link Config} that all the Casdoor service beans are created with.
 */
@ConfigurationProperties(prefix = "casdoor")
public class CasdoorConfiguration extends Config implements InitializingBean {

    private final Security security = new Security();

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(getClientId(), "casdoor.client-id must be set");
        Assert.hasText(getClientSecret(), "casdoor.client-secret must be set");
    }

    public Security getSecurity() {
        return security;
    }

    /**
     * The Spring Security integration.
     */
    public static class Security {

        /**
         * Whether to configure Spring Security (OAuth2 login and resource server) to use Casdoor, when they are on the
         * classpath.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
