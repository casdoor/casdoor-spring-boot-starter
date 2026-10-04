# Casdoor Spring Boot Starter

<p align="center">
  <a href="https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/ci.yml">
    <img alt="CI" src="https://github.com/casdoor/casdoor-spring-boot-starter/actions/workflows/ci.yml/badge.svg">
  </a>
  <a href="https://github.com/casdoor/casdoor-spring-boot-starter/releases/latest">
    <img alt="Release" src="https://img.shields.io/github/release/casdoor/casdoor-spring-boot-starter.svg">
  </a>
  <a href="https://central.sonatype.com/artifact/org.casbin/casdoor-spring-boot-starter">
    <img alt="Maven Central" src="https://img.shields.io/maven-central/v/org.casbin/casdoor-spring-boot-starter.svg">
  </a>
  <a href="https://www.javadoc.io/doc/org.casbin/casdoor-spring-boot-starter">
    <img alt="Javadocs" src="https://www.javadoc.io/badge/org.casbin/casdoor-spring-boot-starter.svg">
  </a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0.txt">
    <img alt="License" src="https://img.shields.io/github/license/casdoor/casdoor-spring-boot-starter.svg?color=blue">
  </a>
  <a href="https://discord.gg/5rPsrAzK7S">
    <img alt="Discord" src="https://img.shields.io/discord/1022748306096537660?logo=discord&label=discord&color=5865F2">
  </a>
</p>

Use [Casdoor](https://casdoor.ai/) in a Spring Boot application with a few properties:

- **Sign users in** with Spring Security OAuth2 login, their Casdoor roles become `ROLE_<name>` authorities.
- **Protect an API** with Casdoor access tokens (JWT resource server), with the same role authorities.
- **Call the Casdoor APIs**: all the services of [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk) (`UserService`, `RoleService`, `EnforcerService`, ...) are beans.

## Requirements

| casdoor-spring-boot-starter | Spring Boot                  | Java |
|-----------------------------|------------------------------|------|
| 2.x                         | 3.0 and later, including 4.x | 17+  |
| 1.x (no longer maintained)  | 2.x                          | 8+   |

## Installation

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

## Configuration

```yaml
casdoor:
  endpoint: https://door.example.com
  client-id: <client-id>
  client-secret: <client-secret>
  certificate: |
    -----BEGIN CERTIFICATE-----
    ...
    -----END CERTIFICATE-----
  organization-name: my-organization
  application-name: my-application
```

| Property                    | Required | Description                                                                                                   |
|-----------------------------|----------|---------------------------------------------------------------------------------------------------------------|
| `casdoor.endpoint`          | Yes      | URL of the Casdoor server. Nothing is configured without it                                                   |
| `casdoor.client-id`         | Yes      | Client ID of the Casdoor application                                                                          |
| `casdoor.client-secret`     | Yes      | Client secret of the Casdoor application                                                                      |
| `casdoor.organization-name` | Yes      | Name of the Casdoor organization, used by the services                                                        |
| `casdoor.application-name`  | Yes      | Name of the Casdoor application, used by the services                                                         |
| `casdoor.certificate`       | No       | Public certificate (PEM) of the cert the application signs its tokens with, used by `AuthService.parseJwtToken()` |
| `casdoor.custom-headers.*`  | No       | HTTP headers added to all the API requests of the services, e.g. `Accept-Language`                            |
| `casdoor.security.enabled`  | No       | Set to `false` to not configure Spring Security, default `true`                                               |

All the values are on the application's edit page in Casdoor. The certificate is on the page of the cert the application uses (Certs → the cert → Certificate).

## Sign In with Casdoor

Add Spring Security's OAuth2 client:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <!-- spring-boot-starter-security-oauth2-client on Spring Boot 4 -->
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

In Casdoor, add `https://<your application>/login/oauth2/code/casdoor` to the Redirect URLs of the application.

That's all: Casdoor is registered with the registration ID `casdoor`, and without your own `SecurityFilterChain`, Spring Boot requires every user to sign in. To choose which pages need it:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests
                    .requestMatchers("/", "/public/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("admin") // the Casdoor role "admin"
                    .anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults());
    return http.build();
}
```

The signed-in user is an `OidcUser`, whose name is the Casdoor user name:

```java
@GetMapping("/profile")
String profile(@AuthenticationPrincipal OidcUser user) {
    return user.getName() + " " + user.getEmail() + " " + user.getAuthorities();
}
```

## Protect an API with Casdoor Access Tokens

Add Spring Security's resource server:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <!-- spring-boot-starter-security-oauth2-resource-server on Spring Boot 4 -->
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
            .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()));
    return http.build();
}
```

Requests need an access token of the Casdoor application: `Authorization: Bearer <access token>`. The token's signature is checked with the keys of Casdoor (`/.well-known/jwks`) and its audience must be the client ID. The authentication's name is the user ID (the `sub` claim), the authorities are the scopes (`SCOPE_<scope>`) and the Casdoor roles (`ROLE_<name>`):

```java
@GetMapping("/api/me")
String me(@AuthenticationPrincipal Jwt jwt) {
    return jwt.getSubject() + " " + jwt.getClaimAsString("name");
}
```

An application can have both, e.g. OAuth2 login for its pages and access tokens for `/api/**`: define two `SecurityFilterChain` beans, the one for the API with `http.securityMatcher("/api/**")` and `@Order(1)`.

### What Is Configured

The `casdoor.*` properties set these Spring Boot properties, only for what is on the classpath:

```properties
spring.security.oauth2.client.registration.casdoor.client-id=${casdoor.client-id}
spring.security.oauth2.client.registration.casdoor.client-secret=${casdoor.client-secret}
spring.security.oauth2.client.registration.casdoor.scope=openid,profile,email
spring.security.oauth2.client.registration.casdoor.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.provider.casdoor.authorization-uri=${casdoor.endpoint}/login/oauth/authorize
spring.security.oauth2.client.provider.casdoor.token-uri=${casdoor.endpoint}/api/login/oauth/access_token
spring.security.oauth2.client.provider.casdoor.user-info-uri=${casdoor.endpoint}/api/userinfo
spring.security.oauth2.client.provider.casdoor.jwk-set-uri=${casdoor.endpoint}/.well-known/jwks
spring.security.oauth2.client.provider.casdoor.user-name-attribute=preferred_username
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${casdoor.endpoint}/.well-known/jwks
spring.security.oauth2.resourceserver.jwt.audiences=${casdoor.client-id}
```

They have the lowest precedence, so any of them can be changed by setting it in your own configuration. The roles are mapped by a `GrantedAuthoritiesMapper` bean (OAuth2 login) and a `JwtAuthenticationConverter` bean (servlet resource server), define your own bean to replace them. Set `casdoor.security.enabled=false` to configure Spring Security yourself.

## Call the Casdoor APIs

Inject the services:

```java
@Autowired
private UserService userService;

@Autowired
private EnforcerService enforcerService;

User alice = userService.getUser("alice");
List<User> users = userService.getUsers();
boolean allowed = enforcerService.enforce("my-organization/read-data", "", "", "", "",
        new Object[]{"my-organization/alice", "data1", "read"});
```

The services call the APIs as the application (client ID and secret). To call them as a user, with the user's own permissions, create a service with the user's access token:

```java
@Autowired
private CasdoorConfiguration casdoorConfiguration;

User account = new UserService(casdoorConfiguration.withAccessToken(accessToken)).getAccount();
```

The services are `AccountService`, `AdapterService`, `ApplicationService`, `AuthService`, `CertService`, `EmailService`, `EnforcerService`, `GroupService`, `InvitationService`, `LdapService`, `MfaService`, `ModelService`, `NotificationService`, `OrderService`, `OrganizationService`, `PaymentService`, `PermissionService`, `PlanService`, `PolicyService`, `PricingService`, `ProductService`, `ProviderService`, `RecordService`, `ResourceService`, `RoleService`, `SessionService`, `SmsService`, `SubscriptionService`, `SyncerService`, `TokenService`, `TransactionService`, `UserService` and `WebhookService`, see the [casdoor-java-sdk README](https://github.com/casdoor/casdoor-java-sdk#readme) for their APIs. Define a bean of the same type to replace one.

Without Spring Security, `AuthService` can do the sign-in itself:

```java
// 1. redirect the user to Casdoor
String url = authService.getSigninUrl("https://<your application>/callback", state);

// 2. in the callback, get the access token with the code and parse it (needs casdoor.certificate)
String accessToken = authService.getOAuthToken(code, state);
User user = authService.parseJwtToken(accessToken);
```

## Upgrading from 1.x

- Spring Boot 3.0+ and Java 17+ are required.
- `CasdoorAutoConfigure` is renamed to `CasdoorAutoConfiguration`, and the beans are named `casdoorUserService`, `casdoorAuthService`, ... Injection by type is not affected.
- Nothing is configured without `casdoor.endpoint`, and the application fails to start without `casdoor.client-id` and `casdoor.client-secret`.
- When Spring Security's OAuth2 client or resource server is on the classpath, it is configured to use Casdoor. Set `casdoor.security.enabled=false` to keep your own configuration as it was.
- The starter doesn't bring Lombok, `spring-test`, JUnit 4 or a fixed version of Jackson into the application anymore.

## Development

```bash
mvn test
```

runs the unit tests. The integration tests (`*IT`) run against a real Casdoor server, started with the data in [.ci/casdoor/init_data.json](.ci/casdoor/init_data.json):

```bash
docker run -d --name casdoor -p 8000:8000 -e driverName=sqlite -e dataSourceName='file:casdoor.db?cache=shared' -e initDataFile=/init_data.json -v "$PWD/.ci/casdoor/init_data.json:/init_data.json:ro" casbin/casdoor-all-in-one
```

```bash
mvn verify
```

The jar is built with the oldest supported Spring Boot (3.0). CI also runs all the tests with the latest Spring Boot 3 and 4 versions, e.g. `mvn verify -Dspring-boot.version=4.1.1`.

Commits to `master` are released by semantic-release from their messages (`feat:` a minor version, `fix:` a patch version): it tags the version, and the tag publishes it to Maven Central.

## Links

- [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk)
- [casdoor-spring-boot-example](https://github.com/casdoor/casdoor-spring-boot-example)
- [Casdoor documentation](https://casdoor.ai/docs/overview)

## License

[Apache License 2.0](LICENSE)
