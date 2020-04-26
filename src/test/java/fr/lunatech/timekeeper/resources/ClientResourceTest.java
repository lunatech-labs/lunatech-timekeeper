package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class ClientResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateClientWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription", emptyList());
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

    @Test
    void shouldNotCreateClientWhenUserProfile() {

        final String token = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownClient() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllClients() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ClientRequest client2 = new ClientRequest("NewClient2", "NewDescription2");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription", emptyList());
        final ClientResponse expectedClient2 = new ClientResponse(2L, "NewClient2", "NewDescription2", emptyList());
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedClient, expectedClient2)));
    }

    @Test
    void shouldFindAllClientsEmpty() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyClientWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .put("/api/clients/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription2", emptyList());
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

    @Test
    void shouldNotModifyClientWhenUserProfile() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .put("/api/clients/1")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}