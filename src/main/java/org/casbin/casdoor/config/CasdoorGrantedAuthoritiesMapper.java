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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

/**
 * Adds a {@code ROLE_<name>} authority for each Casdoor role of the user signed in with OAuth2 login. Users of other
 * OAuth2 providers are left as they are.
 */
public class CasdoorGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final String clientId;

    /**
     * @param clientId the client ID of the Casdoor application, the audience of its ID tokens
     */
    public CasdoorGrantedAuthoritiesMapper(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mapped = new LinkedHashSet<>(authorities);
        for (GrantedAuthority authority : authorities) {
            if (authority instanceof OidcUserAuthority oidcAuthority && isIssuedByCasdoor(oidcAuthority.getIdToken())) {
                mapped.addAll(CasdoorRoles.toAuthorities(oidcAuthority.getAttributes().get(CasdoorRoles.CLAIM)));
            }
        }
        return mapped;
    }

    private boolean isIssuedByCasdoor(OidcIdToken idToken) {
        List<String> audience = idToken.getAudience();
        return clientId != null && audience != null && audience.contains(clientId);
    }
}
