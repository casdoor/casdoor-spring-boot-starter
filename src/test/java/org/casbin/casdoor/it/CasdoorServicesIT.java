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

package org.casbin.casdoor.it;

import org.casbin.casdoor.entity.Role;
import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.service.RoleService;
import org.casbin.casdoor.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApplication.class)
class CasdoorServicesIT {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Test
    void managesUsers() throws Exception {
        assertThat(userService.getUser("admin")).isNotNull();
        assertThat(userService.getUsers()).isNotEmpty();

        String name = "starter_user_" + System.nanoTime();
        User user = new User();
        user.name = name;
        user.displayName = name;
        assertThat(userService.addUser(user).getData()).isEqualTo("Affected");

        User added = userService.getUser(name);
        added.displayName = "Updated Starter User";
        assertThat(userService.updateUser(added).getData()).isEqualTo("Affected");
        assertThat(userService.getUser(name).displayName).isEqualTo("Updated Starter User");

        assertThat(userService.deleteUser(added).getData()).isEqualTo("Affected");
        assertThat(userService.getUser(name)).isNull();
    }

    @Test
    void managesRoles() throws Exception {
        String name = "starter_role_" + System.nanoTime();
        assertThat(roleService.addRole(new Role(null, name, "", name, "Casdoor Website")).getData()).isEqualTo("Affected");
        assertThat(roleService.getRole(name).name).isEqualTo(name);
        assertThat(roleService.deleteRole(roleService.getRole(name)).getData()).isEqualTo("Affected");
    }
}
