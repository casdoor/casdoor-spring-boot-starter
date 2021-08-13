# Casdoor SpringBoot Starter

[![License](https://img.shields.io/github/license/casdoor/casdoor-spring-boot-starter.svg?style=flat-square&color=blue)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![SpringBootVersion](https://img.shields.io/badge/SpringBoot-2.5.2-heightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![CasdoorSdkVersion](https://img.shields.io/badge/CasdoorSdkVersion-1.0.0-heightgreen.svg?style=flat-square)](https://github.com/casdoor/casdoor-java-sdk)

[![](https://raw.githubusercontent.com/casbin/jcasbin/master/casbin-logo.png)](https://casbin.org)

## English

Casdoor SpringBoot Starter is designed to help you easily integrate [casdoor](https://github.com/casbin/casdoor) into
your Spring Boot project.

## how to use

1. Add ```casdoor-spring-boot-starter``` to the Spring Boot project.

```Maven```

No central library, please download by yourself


2. Inject the Enforcer where you need to use it

```java

@Component
public class Test {
    @Autowired
    private Casdoor casdoor;
}
```

3. Add configuration

```yaml
casbin:
  endpoint: https://xxx.xxx.xxx
```
