# Casdoor Spring Boot Starter

<p align="center">
  <a href="#badge">
    <img alt="semantic-release" src="https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg">
  </a>
  <a href="https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/maven-ci.yml">
    <img alt="build" src="https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/maven-ci.yml/badge.svg">
  </a>
  <a href="https://github.com/casdoor/casdoor-spring-boot-starter/releases/latest">
    <img alt="Release" src="https://img.shields.io/github/release/casdoor/casdoor-spring-boot-starter.svg">
  </a>
  <a href="https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter/latest">
    <img alt="Maven Central" src="https://img.shields.io/maven-central/v/org.casbin/casdoor-spring-boot-starter.svg">
  </a>
  <a href="https://www.javadoc.io/doc/org.casbin/casdoor-spring-boot-starter">
    <img alt="Javadocs" src="https://www.javadoc.io/badge/org.casbin/casdoor-spring-boot-starter.svg">
  </a>
</p>

<p align="center">
  <a href="http://www.apache.org/licenses/LICENSE-2.0.txt">
    <img alt="License" src="https://img.shields.io/github/license/casdoor/casdoor-spring-boot-starter.svg?style=flat-square&color=blue">
  </a>
  <a href="https://github.com/casdoor/casdoor-spring-boot-starter/issues">
    <img alt="GitHub issues" src="https://img.shields.io/github/issues/casdoor/casdoor-spring-boot-starter?style=flat-square">
  </a>
  <a href="CODE_OF_CONDUCT.md">
    <img alt="Contributor Covenant" src="https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg?style=flat-square">
  </a>
  <a href="https://discord.gg/5rPsrAzK7S">
    <img alt="Casdoor" src="https://img.shields.io/discord/1022748306096537660?style=flat-square&logo=discord&label=discord&color=5865F2">
  </a>
</p>

Casdoor Spring Boot Starter integrates [Casdoor](https://casdoor.ai/) into your Spring Boot application. It configures [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk) from your application properties and registers all its services as Spring beans, so you can inject `AuthService`, `UserService`, `RoleService`, ... anywhere.

The services have the same features as [casdoor-go-sdk](https://github.com/casdoor/casdoor-go-sdk), see the [casdoor-java-sdk README](https://github.com/casdoor/casdoor-java-sdk#readme) for all the APIs.

## 📦 Installation

Maven:

```xml
<dependency>
    <groupId>org.casbin</groupId>
    <artifactId>casdoor-spring-boot-starter</artifactId>
    <version>${casdoor-spring-boot-starter.version}</version>
</dependency>
```

Gradle:

```groovy
implementation 'org.casbin:casdoor-spring-boot-starter:<version>'
```

The latest version is shown by the Maven Central badge above.

## ⚙️ Configuration

| Property                  | Required | Description                                                                 |
|---------------------------|----------|-----------------------------------------------------------------------------|
| casdoor.endpoint          | Yes      | Casdoor server URL, such as `http://localhost:8000`                         |
| casdoor.client-id         | Yes      | Client ID of the Casdoor application                                        |
| casdoor.client-secret     | Yes      | Client secret of the Casdoor application                                    |
| casdoor.certificate       | Yes      | x509 certificate (PEM) of the application's cert, used to verify JWT tokens |
| casdoor.organization-name | Yes      | Name of the Casdoor organization                                            |
| casdoor.application-name  | Yes      | Name of the Casdoor application                                             |
| casdoor.custom-headers.*  | No       | HTTP headers added to all the API requests, e.g. `Accept-Language`          |

`application.yml`:

```yaml
casdoor:
  endpoint: http://localhost:8000
  client-id: <client-id>
  client-secret: <client-secret>
  certificate: |
    -----BEGIN CERTIFICATE-----
    ...
    -----END CERTIFICATE-----
  organization-name: my-organization
  application-name: my-application
  custom-headers:
    Accept-Language: de
```

`application.properties`:

```properties
casdoor.endpoint = http://localhost:8000
casdoor.client-id = <client-id>
casdoor.client-secret = <client-secret>
casdoor.certificate = <certificate>
casdoor.organization-name = my-organization
casdoor.application-name = my-application
```

All the values are on the application's edit page in Casdoor. The certificate is the public certificate of the cert the application uses (Certs page → the cert → "Certificate").

## 🚀 Usage

Inject the services you need:

```java
@Resource
private AuthService authService;

@Resource
private UserService userService;
```

### Sign the User In

```java
// 1. Redirect the user to Casdoor
String url = authService.getSigninUrl("http://localhost:8080/callback", state);

// 2. In the callback, exchange the code for the access token and verify it
String accessToken = authService.getOAuthToken(code, state);
User user = authService.parseJwtToken(accessToken);
```

### Call the APIs

```java
User alice = userService.getUser("alice");
List<User> users = userService.getUsers();
roleService.addRole(new Role(null, "admin", createdTime, "Administrator", ""));
boolean allowed = enforcerService.enforce("my-organization/read-data", "", "", "", "",
        new Object[]{"my-organization/alice", "data1", "read"});
```

The services call the APIs as the application (client ID and secret). To call them as the signed-in user, with the user's own permissions, create a service with the user's access token:

```java
@Resource
private CasdoorConfiguration casdoorConfiguration;

User account = new UserService(casdoorConfiguration.withAccessToken(accessToken)).getAccount();
```

### Available Services

`AccountService`, `AdapterService`, `ApplicationService`, `AuthService`, `CertService`, `EmailService`, `EnforcerService`, `GroupService`, `InvitationService`, `LdapService`, `MfaService`, `ModelService`, `NotificationService`, `OrderService`, `OrganizationService`, `PaymentService`, `PermissionService`, `PlanService`, `PolicyService`, `PricingService`, `ProductService`, `ProviderService`, `RecordService`, `ResourceService`, `RoleService`, `SessionService`, `SmsService`, `SubscriptionService`, `SyncerService`, `TokenService`, `TransactionService`, `UserService` and `WebhookService`.

Each bean is `@ConditionalOnMissingBean`, so you can replace any of them with your own.

## 🛠 Development

The tests run against a real Casdoor server. CI starts one with Docker and the data in [.ci/casdoor/init_data.json](.ci/casdoor/init_data.json):

```bash
docker run -d --name casdoor -p 8000:8000 \
  -e driverName=sqlite \
  -e dataSourceName='file:casdoor.db?cache=shared' \
  -e initDataFile=/init_data.json \
  -v "$PWD/.ci/casdoor/init_data.json:/init_data.json:ro" \
  casbin/casdoor-all-in-one

mvn test
```

Releases are published to Maven Central automatically by semantic-release when commits are pushed to `master`.

## 📖 More

- [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk)
- [casdoor-spring-boot-example](https://github.com/casdoor/casdoor-spring-boot-example)
- [casdoor-spring-boot-shiro-example](https://github.com/casdoor/casdoor-spring-boot-shiro-example)
- [Spring Security integration](https://casdoor.ai/docs/category/spring-security/)
- [Casdoor Documentation](https://casdoor.ai/docs/overview)

## 📄 License

This project is licensed under the Apache License 2.0.
