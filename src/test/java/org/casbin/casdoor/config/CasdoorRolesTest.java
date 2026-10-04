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

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import static org.assertj.core.api.Assertions.assertThat;

class CasdoorRolesTest {

    private static List<String> names(Collection<? extends GrantedAuthority> authorities) {
        return AuthorityUtils.authorityListToSet(authorities).stream().sorted().toList();
    }

    private static OidcIdToken idToken(String audience) {
        return OidcIdToken.withTokenValue("token")
                .subject("user-id")
                .audience(List.of(audience))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
    }

    @Test
    void readsRoleNamesAndRoleObjects() {
        assertThat(names(CasdoorRoles.toAuthorities(List.of("admin", "editor")))).containsExactly("ROLE_admin", "ROLE_editor");
        assertThat(names(CasdoorRoles.toAuthorities(List.of(Map.of("owner", "org", "name", "admin"), Map.of("owner", "org")))))
                .containsExactly("ROLE_admin");
        assertThat(CasdoorRoles.toAuthorities(null)).isEmpty();
        assertThat(CasdoorRoles.toAuthorities("admin")).isEmpty();
    }

    @Test
    void mapsTheRolesOfCasdoorUsers() {
        CasdoorGrantedAuthoritiesMapper mapper = new CasdoorGrantedAuthoritiesMapper("client-id");
        OidcUserInfo userInfo = new OidcUserInfo(Map.of("sub", "user-id", "roles", List.of("admin")));
        OidcUserAuthority casdoorUser = new OidcUserAuthority(idToken("client-id"), userInfo);
        OidcUserAuthority otherUser = new OidcUserAuthority(idToken("another-client-id"), userInfo);
        SimpleGrantedAuthority scope = new SimpleGrantedAuthority("SCOPE_openid");

        assertThat(names(mapper.mapAuthorities(List.of(casdoorUser, scope)))).containsExactly("OIDC_USER", "ROLE_admin", "SCOPE_openid");
        assertThat(names(mapper.mapAuthorities(List.of(otherUser, scope)))).containsExactly("OIDC_USER", "SCOPE_openid");
    }

    @Test
    void convertsTheRolesAndScopesOfAccessTokens() {
        JwtAuthenticationConverter converter = new CasdoorSecurityAutoConfiguration.ResourceServerConfiguration()
                .casdoorJwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "RS256")
                .subject("user-id")
                .claim("scope", "openid profile")
                .claim("roles", List.of(Map.of("owner", "org", "name", "admin")))
                .build();

        // Spring Security 7 also adds FACTOR_BEARER
        assertThat(names(converter.convert(jwt).getAuthorities())).contains("ROLE_admin", "SCOPE_openid", "SCOPE_profile");
    }
}
