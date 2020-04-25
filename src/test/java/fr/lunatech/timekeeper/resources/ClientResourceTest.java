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
    void shouldCreateClient() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription", emptyList());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

    @Test
    void shouldCreateClientIgnoreUselessParams() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"projectsId\":[1,2,3],\"id\":9999,\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\",\"projectsId\":[]}"));
    }

    @Test
    void shouldNotFindUnknownClient() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllClients() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ClientRequest client2 = new ClientRequest("NewClient2", "NewDescription2");
        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription", emptyList());
        final ClientResponse expectedClient2 = new ClientResponse(2L, "NewClient2", "NewDescription2", emptyList());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedClient, expectedClient2)));
    }

    @Test
    void shouldFindAllClientsEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyClient() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");

        final ClientResponse expectedClient2 = new ClientResponse(1L, "NewClient", "NewDescription2", emptyList());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .put("/api/clients/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient2)));
    }

    @Test
    void shouldModifyClientIgnoreUselessParams() {

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"projectsId\":[1,2,3],\"id\":9999,\"name\":\"NewName\",\"description\":\"NewDescription2\"}")
                .put("/api/clients/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\",\"projectsId\":[]}"));
    }
}