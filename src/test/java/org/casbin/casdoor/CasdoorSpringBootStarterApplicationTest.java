package org.casbin.casdoor;

import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.service.AuthService;
import org.casbin.casdoor.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {org.casbin.casdoor.config.CasdoorAutoConfigure.class})
@TestPropertySource("classpath:test.properties")
public class CasdoorSpringBootStarterApplicationTest {
    @Resource
    UserService userService;

    @Resource
    AuthService authService;

    @Test
    public void testInjection() {
        Assert.assertNotNull(authService);
        Assert.assertNotNull(userService);
    }

    @Test
    public void testGetUser() throws Exception {
        Assert.assertNotNull(userService.getUser("admin"));
    }

    @Test
    public void testGetUsers() throws Exception {
        Assert.assertNotNull(userService.getUsers());
    }

    @Test
    public void testAddUser() throws Exception {
        User user = userService.getUser("admin");
        user.name = "testAdmin";
        user.id = "qwertyuio";
        Assert.assertEquals("Affected", userService.addUser(user).getData());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = userService.getUser("testAdmin");
        user.displayName = "testUpdateAdmin";
        Assert.assertEquals("Affected", userService.updateUser(user).getData());
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = userService.getUser("testAdmin");
        Assert.assertEquals("Affected", userService.deleteUser(user).getData());
    }
}
