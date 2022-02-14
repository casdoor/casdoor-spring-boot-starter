# Casdoor SpringBoot Starter

[![build](https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/maven-ci.yml)
[![License](https://img.shields.io/github/license/casdoor/casdoor-spring-boot-starter.svg?style=flat-square&color=blue)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](code_of_conduct.md)
[![SpringBootVersion](https://img.shields.io/badge/SpringBoot-2.5.2-heightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![Javadocs](https://www.javadoc.io/badge/org.casbin/casdoor-spring-boot-starter.svg)](https://www.javadoc.io/doc/org.casbin/casdoor-spring-boot-starter)
[![Maven Central](https://img.shields.io/maven-central/v/org.casbin/casdoor-spring-boot-starter.svg)](https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter/latest)
[![Release](https://img.shields.io/github/release/casdoor/casdoor-spring-boot-starter.svg)](https://github.com/casdoor/casdoor-spring-boot-starter/releases/latest)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/casbin/lobby)

Casdoor SpringBoot Starter is designed to help you easily integrate [Casdoor](https://github.com/casbin/casdoor) into
your Spring Boot project.

## What you need

The Casdoor should be deployed.

You can refer to the Casdoor official documentation for the [Server Installation](/docs/basic/server-installation).

After a successful deployment, you need to ensure:

- The Casdoor server is successfully running on **http://localhost:8000**.
- Open your favorite browser and visit **http://localhost:7001**, you will see the login page of Casdoor.
- Input `admin` and `123` to test login functionality is working fine.


## Quickstart

### Include the dependency

Add ```casdoor-spring-boot-starter``` to the Spring Boot project.

For Apache Maven:

```Maven
<!-- https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter -->
<dependency>
    <groupId>org.casbin</groupId>
    <artifactId>casdoor-spring-boot-starter</artifactId>
    <version>1.x.y</version>
</dependency>
```

For Gradle:

```gradle
// https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter
implementation group: 'org.casbin', name: 'casdoor-spring-boot-starter', version: '1.x.y'
```

### Configure your properties

Initialization requires 6 parameters, which are all string type.

| Name (in order)  | Must | Description                                         |
|------------------|------|-----------------------------------------------------|
| endpoint         | Yes  | Casdoor Server Url, such as `http://localhost:8000` |
| clientId         | Yes  | Application.client_id                               |
| clientSecret     | Yes  | Application.client_secret                           |
| jwtPublicKey     | Yes  | The public key for the Casdoor application's cert   |
| organizationName | Yes  | Application.organization                            |
| applicationName  | No   | Application.name                                    |

You can use Java properties or YAML files to init as below.

For properties:

```properties
casdoor.endpoint = http://localhost:8000
casdoor.clientId = <client-id>
casdoor.clientSecret = <client-secret>
casdoor.jwtPublicKey = <jwt-public-key>
casdoor.organizationName = built-in
casdoor.applicationName = app-built-in
```

For yaml:

```yaml
casdoor:
  endpoint: http://localhost:8000
  client-id: <client-id>
  client-secret: <client-secret>
  jwt-public-key: <jwt-public-key>
  organization-name: built-in
  application-name: app-built-in
```

### Get the Service and use

Now provide 5 services: `CasdoorAuthService`, `CasdoorUserService`, `CasdoorEmailService`, `CasdoorSmsService` and `CasdoorResourceService`.

You can create them as below in SpringBoot project.

```java
@Resource
private CasdoorAuthService casdoorAuthService;
```

Examples of APIs are shown below.

- CasdoorAuthService
  - `String token = casdoorAuthService.getOAuthToken(code, "app-built-in");`
  - `CasdoorUser casdoorUser = casdoorAuthService.parseJwtToken(token);`
- CasdoorUserService
  - `CasdoorUser casdoorUser = casdoorUserService.getUser("admin");`
  - `CasdoorUser casdoorUser = casdoorUserService.getUserByEmail("admin@example.com");`
  - `CasdoorUser[] casdoorUsers = casdoorUserService.getUsers();`
  - `CasdoorUser[] casdoorUsers = casdoorUserService.getSortedUsers("created_time", 5);`
  - `int count = casdoorUserService.getUserCount("0");`
  - `CasdoorResponse response = casdoorUserService.addUser(user);`
  - `CasdoorResponse response = casdoorUserService.updateUser(user);`
  - `CasdoorResponse response = casdoorUserService.deleteUser(user);`
- CasdoorEmailService
  - `CasdoorResponse response = casdoorEmailService.sendEmail(title, content, sender, receiver);`
- CasdoorSmsService
  - `CasdoorResponse response = casdoorSmsService.sendSms(randomCode(), receiver);`
- CasdoorResourceService
  - `CasdoorResponse response = casdoorResourceService.uploadResource(user, tag, parent, fullFilePath, file);`
  - `CasdoorResponse response = casdoorResourceService.deleteResource(file.getName());`

## What's more

You can explore the following projects/docs to learn more about the integration of Java with Casdoor.

- [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk)
- [casdoor-spring-boot-example](https://github.com/casdoor/casdoor-spring-boot-example)
- [casdoor-spring-boot-security-example](https://casdoor.org/docs/integration/spring-security)
- [casdoor-spring-boot-shiro-example](https://github.com/casdoor/casdoor-spring-boot-shiro-example)
