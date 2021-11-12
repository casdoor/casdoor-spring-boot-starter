package org.casbin.casdoor;

import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorAuthService;
import org.casbin.casdoor.service.CasdoorUserService;
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
    CasdoorUserService casdoorUserService;

    @Resource
    CasdoorAuthService casdoorAuthService;

    @Test
    public void testInjection() {
        Assert.assertNotNull(casdoorAuthService);
        Assert.assertNotNull(casdoorUserService);
    }

    @Test
    public void testGetUser() throws Exception {
        Assert.assertNotNull(casdoorUserService.getUser("admin"));
    }

    @Test
    public void testGetUsers() throws Exception {
        Assert.assertNotNull(casdoorUserService.getUsers());
    }

    @Test
    public void testAddUser() throws Exception {
        CasdoorUser user = casdoorUserService.getUser("admin");
        user.setName("testAdmin");
        user.setId("qwertyuio");
        Assert.assertEquals("Affected", casdoorUserService.addUser(user).getData());
    }

    @Test
    public void testUpdateUser() throws Exception {
        CasdoorUser user = casdoorUserService.getUser("testAdmin");
        user.setDisplayName("testUpdateAdmin");
        Assert.assertEquals("Affected", casdoorUserService.updateUser(user).getData());
    }

    @Test
    public void testDeleteUser() throws Exception {
        CasdoorUser user = casdoorUserService.getUser("testAdmin");
        Assert.assertEquals("Affected", casdoorUserService.deleteUser(user).getData());
    }
}
