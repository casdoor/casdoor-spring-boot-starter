package org.casbin.casdoor;

import org.casbin.casdoor.entity.Role;
import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;

@SpringBootTest(classes = {org.casbin.casdoor.config.CasdoorAutoConfigure.class})
@TestPropertySource("classpath:test.properties")
public class CasdoorSpringBootStarterApplicationTest {

    @Resource
    UserService userService;

    @Resource
    AuthService authService;

    @Resource
    RoleService roleService;

    @Resource
    org.springframework.context.ApplicationContext context;

    @Test
    public void testInjection() {
        Assertions.assertNotNull(authService);
        Assertions.assertNotNull(userService);
        for (Class<?> service : new Class<?>[]{OrderService.class, MfaService.class, NotificationService.class,
                PolicyService.class, TransactionService.class, InvitationService.class, LdapService.class}) {
            Assertions.assertNotNull(context.getBean(service));
        }
    }

    @Test
    public void testUser() throws Exception {
        Assertions.assertNotNull(userService.getUser("admin"));
        Assertions.assertFalse(userService.getUsers().isEmpty());

        String name = "starter_user_" + System.nanoTime();
        User user = new User();
        user.name = name;
        user.displayName = name;
        Assertions.assertEquals("Affected", userService.addUser(user).getData());

        User added = userService.getUser(name);
        added.displayName = "Updated Starter User";
        Assertions.assertEquals("Affected", userService.updateUser(added).getData());
        Assertions.assertEquals("Updated Starter User", userService.getUser(name).displayName);

        Assertions.assertEquals("Affected", userService.deleteUser(added).getData());
        Assertions.assertNull(userService.getUser(name));
    }

    @Test
    public void testRole() throws Exception {
        String name = "starter_role_" + System.nanoTime();
        Assertions.assertEquals("Affected", roleService.addRole(new Role(null, name, "", name, "Casdoor Website")).getData());
        Assertions.assertEquals(name, roleService.getRole(name).name);
        Assertions.assertEquals("Affected", roleService.deleteRole(roleService.getRole(name)).getData());
    }
}
