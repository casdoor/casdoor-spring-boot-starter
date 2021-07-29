package org.casbin.casdoor;

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
    Casdoor casdoor;

    @Test
    public void getSystemTime() {
        Assert.assertNotNull(casdoor);
    }
}
