package fr.lunatech.timekeeper.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.keycloak.util.JsonSerialization;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String KEYCLOAK_SERVER_URL = "http://localhost:8180/auth";
    private static final String KEYCLOAK_REALM = "Timekeeper";
    private static final String KEYCLOAK_CLIENT = "timekeeper-quarkus-backend";
    private static final String KEYCLOAK_SECRET = "secret-123456789";

    // Mount keycloak docker image
    private static final GenericContainer KEYCLOAK = new FixedHostPortGenericContainer("jboss/keycloak")
            .withFixedExposedPort(8180, 8080)
            .withEnv("DB_VENDOR", "H2")
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .waitingFor(Wait.forHttp("/auth").withStartupTimeout(Duration.ofSeconds(120L)));

    static {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Override
    public Map<String, String> start() {
        if (StringUtils.trimToEmpty(System.getenv("ENV")).equalsIgnoreCase("fast-test-only")) {
            System.out.println("[INFO] \uD83D\uDD25 Fast test env variable is set, do not start Keycloak docker.");
            return Collections.emptyMap();
        }
        // Start keycloak docker image
        // We cannot use SLF4J here. This is why you see System.out.println.
        long start = System.currentTimeMillis();
        System.out.println("[INFO] \uD83D\uDE9A Starting Keycloack as a Docker container for tests...");
        KEYCLOAK.start();
        System.out.println(String.format("[INFO] ⭐  Keycloak started in %d ms", System.currentTimeMillis() - start));

        RealmRepresentation realm = createRealm(KEYCLOAK_REALM);

        realm.getClients().add(createClient(KEYCLOAK_CLIENT));
        realm.getUsers().add(createUser("jimmy", "Jimmy", "James", "lunatech.fr", "user"));
        realm.getUsers().add(createUser("merry", "Merry", "Jones", "lunatech.fr", "user"));
        realm.getUsers().add(createUser("sam", "Sam", "Uell", "lunatech.fr", "admin"));
        realm.getUsers().add(createUser("clark", "Clark", "Kent", "lunatech.fr", "super_admin"));


        // Create config in keycloak
        try {
            RestAssured
                    .given()
                    .auth().preemptive().oauth2(getKeycloakAdminAccessToken())
                    .contentType("application/json")
                    .body(JsonSerialization.writeValueAsBytes(realm))
                    .when()
                    .post(KEYCLOAK_SERVER_URL + "/admin/realms").then()
                    .statusCode(201);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyMap();
    }

    public static String getUserAccessToken() {
        return getAccessToken("jimmy");
    }

    public static String getUser2AccessToken() {
        return getAccessToken("merry");
    }

    public static String getAdminAccessToken() {
        return getAccessToken("sam");
    }

    public static String getSuperAdminAccessToken() {
        return getAccessToken("clark");
    }

    public static String getAccessToken(String userName) {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", KEYCLOAK_CLIENT)
                .param("client_secret", KEYCLOAK_SECRET)
                .when()
                .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

    private static String getKeycloakAdminAccessToken() {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", "admin")
                .param("password", "admin")
                .param("client_id", "admin-cli")
                .when()
                .post(KEYCLOAK_SERVER_URL + "/realms/master/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

    private static RealmRepresentation createRealm(String name) {
        RealmRepresentation realm = new RealmRepresentation();

        realm.setRealm(name);
        realm.setEnabled(true);
        realm.setUsers(new ArrayList<>());
        realm.setClients(new ArrayList<>());
        realm.setAccessTokenLifespan(3);

        RolesRepresentation roles = new RolesRepresentation();
        List<RoleRepresentation> realmRoles = new ArrayList<>();

        roles.setRealm(realmRoles);
        realm.setRoles(roles);

        realm.getRoles().getRealm().add(new RoleRepresentation("user", null, false));
        realm.getRoles().getRealm().add(new RoleRepresentation("admin", null, false));

        return realm;
    }

    private static ClientRepresentation createClient(String clientId) {
        ClientRepresentation client = new ClientRepresentation();

        client.setClientId(clientId);
        client.setPublicClient(false);
        client.setSecret(KEYCLOAK_SECRET);
        client.setDirectAccessGrantsEnabled(true);
        client.setEnabled(true);

        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setName("organization");
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");
        mapper.setConfig(Map.of(
                "userinfo.token.claim", "true",
                "user.attribute", "organization",
                "id.token.claim", "true",
                "access.token.claim", "true",
                "claim.name", "organization",
                "jsonType.label", "String"
        ));
        client.setProtocolMappers(List.of(mapper));
        client.setEnabled(true);


        return client;
    }

    private static UserRepresentation createUser(String username, String firstname, String lastname, String organization, String... realmRoles) {
        UserRepresentation user = new UserRepresentation();

        user.setUsername(username);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEnabled(true);
        user.setCredentials(new ArrayList<>());
        user.setRealmRoles(Arrays.asList(realmRoles));
        user.setEmail(username + "@" + organization);
        user.singleAttribute("organization", organization)
                .singleAttribute("picture", username + ".png");

        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(username);
        credential.setTemporary(false);

        user.getCredentials().add(credential);

        return user;
    }

    @Override
    public void stop() {
        // Stop keycloak docker image
        KEYCLOAK.stop();
    }
}