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

import java.net.CookieManager;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.casbin.casdoor.config.CasdoorConfiguration;
import org.casbin.casdoor.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Signs in to {@link TestApplication} with Casdoor and calls its API with a Casdoor access token.
 */
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CasdoorSecurityIT {

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String ROLE = "ROLE_starter-ci-role";

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Autowired
    private Environment environment;

    @Autowired
    private CasdoorConfiguration casdoorConfiguration;

    @Test
    void signsInWithOAuth2Login() throws Exception {
        HttpResponse<String> authorization = get(app("/oauth2/authorization/casdoor"), null);
        assertThat(authorization.statusCode()).isEqualTo(302);
        URI authorize = URI.create(location(authorization));
        assertThat(authorize.toString()).startsWith(casdoor("/login/oauth/authorize?"));
        Map<String, String> parameters = query(authorize);
        assertThat(parameters.get("client_id")).isEqualTo(casdoorConfiguration.getClientId());
        assertThat(parameters.get("scope")).isEqualTo("openid profile email");

        // sign in like the Casdoor login page does, which gets the authorization code
        Map<String, String> loginParameters = new LinkedHashMap<>();
        loginParameters.put("clientId", parameters.get("client_id"));
        loginParameters.put("responseType", "code");
        loginParameters.put("redirectUri", parameters.get("redirect_uri"));
        loginParameters.put("scope", parameters.get("scope"));
        loginParameters.put("state", parameters.get("state"));
        loginParameters.put("nonce", parameters.getOrDefault("nonce", ""));
        loginParameters.put("code_challenge", parameters.getOrDefault("code_challenge", ""));
        loginParameters.put("code_challenge_method", parameters.getOrDefault("code_challenge_method", ""));
        String loginBody = JSON.writeValueAsString(Map.of(
                "application", casdoorConfiguration.getApplicationName(),
                "organization", "built-in",
                "username", "admin",
                "password", "123",
                "type", "code",
                "signinMethod", "Password",
                "autoSignin", true));
        HttpResponse<String> login = client.send(HttpRequest.newBuilder(URI.create(casdoor("/api/login?") + form(loginParameters)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build(), HttpResponse.BodyHandlers.ofString());
        JsonNode loginResult = JSON.readTree(login.body());
        assertThat(loginResult.path("status").asText()).as(login.body()).isEqualTo("ok");
        String code = loginResult.path("data").asText();

        HttpResponse<String> callback = get(parameters.get("redirect_uri") + "?"
                + form(Map.of("code", code, "state", parameters.get("state"))), null);
        assertThat(callback.statusCode()).as(callback.body()).isEqualTo(302);
        assertThat(location(callback)).doesNotContain("error");

        HttpResponse<String> me = get(app("/me"), null);
        assertThat(me.statusCode()).isEqualTo(200);
        JsonNode user = JSON.readTree(me.body());
        assertThat(user.path("name").asText()).isEqualTo("admin");
        assertThat(authorities(user)).contains("OIDC_USER", "SCOPE_openid", ROLE);
    }

    @Test
    void acceptsCasdoorAccessTokens() throws Exception {
        String accessToken = accessToken();

        HttpResponse<String> me = get(app("/api/me"), accessToken);
        assertThat(me.statusCode()).as(me.body()).isEqualTo(200);
        JsonNode user = JSON.readTree(me.body());
        assertThat(user.path("name").asText()).isNotEmpty();
        assertThat(authorities(user)).contains("SCOPE_openid", ROLE);

        // the SDK services can also call the APIs as the user
        assertThat(new UserService(casdoorConfiguration.withAccessToken(accessToken)).getAccount().name).isEqualTo("admin");
    }

    @Test
    void rejectsRequestsWithoutValidAccessTokens() throws Exception {
        assertThat(get(app("/api/me"), null).statusCode()).isEqualTo(401);
        assertThat(get(app("/api/me"), "invalid").statusCode()).isEqualTo(401);
    }

    private String accessToken() throws Exception {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("client_id", casdoorConfiguration.getClientId());
        parameters.put("client_secret", casdoorConfiguration.getClientSecret());
        parameters.put("username", "admin");
        parameters.put("password", "123");
        parameters.put("scope", "openid profile");
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(URI.create(casdoor("/api/login/oauth/access_token")))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form(parameters)))
                .build(), HttpResponse.BodyHandlers.ofString());
        String accessToken = JSON.readTree(response.body()).path("access_token").asText();
        assertThat(accessToken).as(response.body()).isNotEmpty();
        return accessToken;
    }

    private HttpResponse<String> get(String uri, String accessToken) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(uri)).GET();
        if (accessToken != null) {
            request.header("Authorization", "Bearer " + accessToken);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String app(String path) {
        return "http://localhost:" + environment.getProperty("local.server.port") + path;
    }

    private String casdoor(String path) {
        return casdoorConfiguration.getEndpoint() + path;
    }

    private static String location(HttpResponse<?> response) {
        return response.headers().firstValue("Location").orElseThrow();
    }

    private static List<String> authorities(JsonNode user) {
        return JSON.convertValue(user.path("authorities"), JSON.getTypeFactory().constructCollectionType(List.class, String.class));
    }

    private static Map<String, String> query(URI uri) {
        Map<String, String> parameters = new LinkedHashMap<>();
        for (String parameter : uri.getRawQuery().split("&")) {
            String[] pair = parameter.split("=", 2);
            parameters.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "");
        }
        return parameters;
    }

    private static String form(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
