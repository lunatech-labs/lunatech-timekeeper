package fr.lunatech.timekeeper.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
class ActivityResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateActivityWithoutMembers() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":1}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when().get("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"billable\":true,\"customerId\":1,\"description\":\"New project\",\"id\":2,\"membersId\":[],\"name\":\"Pepito\"}"));
    }

    @Test
    void shouldCreateActivityWithMembers() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":\"true\",\"description\":\"New project\",\"customerId\":2}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("3"));

        given()
                .when().get("/api/activities/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"billable\":true,\"customerId\":2,\"description\":\"New project\",\"id\":3,\"membersId\":[],\"name\":\"Pepito\"}"));
    }

    @Test
    void shouldNotCreateActivityWithUnknownCustomer() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":10}")
                .post("/api/activities")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownActivity() {
        given()
                .when().get("/api/activities/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyActivity() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":1}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"NewClient2\",\"description\":\"NewDescription2\"}")
                .post("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("3"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito2\",\"billable\":false,\"description\":\"New project2\",\"customerId\":3}")
                .put("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when()
                .get("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"billable\":false,\"customerId\":3,\"description\":\"New project2\",\"id\":2,\"membersId\":[],\"name\":\"Pepito2\"}"));
    }

    @Test
    void shouldRemoveActivity() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":1}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));

        given()
                .when()
                .get("/api/activities/1")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("2"));
    }
}