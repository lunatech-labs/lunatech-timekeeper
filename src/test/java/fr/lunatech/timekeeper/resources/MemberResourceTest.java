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
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
class MemberResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldAddMemberToActivity() {
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
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("3"));


        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));

        given()
                .when()
                .get("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"id\":4,\"role\":\"Developer\",\"userId\":1}"));
    }

    @Test
    void shouldNotAddMemberToActivityWithUnknownUser() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":12}")
                .post("/api/members")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownMember() {
        given()
                .when()
                .get("/api/members/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyMember() {
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
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("3"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"role\":\"TeamLeader\"}")
                .put("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));

        given()
                .when()
                .get("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"id\":4,\"role\":\"TeamLeader\",\"userId\":1}"));
    }

    @Test
    void shouldRemodeMember() {
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
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("3"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));

        given()
                .when().get("/api/activities/3/members/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("4"));
    }
}