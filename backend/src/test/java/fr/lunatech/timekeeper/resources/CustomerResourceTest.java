package fr.lunatech.timekeeper.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
class CustomerResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateCustomer() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"}"));
    }

    @Test
    void shouldCreateCustomerIgnoreUselessParams() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"activitiesId\":[1,2,3],\"id\":9999,\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"}"));
    }

    @Test
    void shouldNotFindUnknownCustomer() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllCustomers() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient2\",\"description\":\"NewDescription2\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"},{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":2,\"name\":\"NewClient2\"}]"));
    }

    @Test
    void shouldFindAllCustomersEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyCustomer() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"activitiesId\":[],\"id\":1,\"name\":\"NewName\",\"description\":\"NewDescription2\"}")
                .put("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\"}"));
    }

    @Test
    void shouldModifyCustomerIgnoreUselessParams() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"activitiesId\":[1,2,3],\"id\":9999,\"name\":\"NewName\",\"description\":\"NewDescription2\"}")
                .put("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\"}"));
    }

    @Test
    void shouldRemoveCustomer() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .delete("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when()
                .delete("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}