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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Reads the Casdoor roles of a user from the {@code roles} claim, which is a list of role names in the userinfo and in
 * the "JWT-Standard" tokens, and a list of role objects in the "JWT" tokens.
 */
final class CasdoorRoles {

    static final String CLAIM = "roles";

    static final String AUTHORITY_PREFIX = "ROLE_";

    private CasdoorRoles() {
    }

    static List<GrantedAuthority> toAuthorities(Object claim) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (!(claim instanceof Collection<?> roles)) {
            return authorities;
        }
        for (Object role : roles) {
            Object name = role instanceof Map<?, ?> map ? map.get("name") : role;
            if (name instanceof String roleName && !roleName.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + roleName));
            }
        }
        return authorities;
    }
}
