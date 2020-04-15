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
class UserResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateUser() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"email\":\"sam@gmail.com\",\"firstName\":\"Sam\",\"id\":1,\"lastName\":\"Huel\",\"profiles\":[\"Admin\"]}"));
    }

    @Test
    void shouldNotFindUnknownUser() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyUser() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam2\",\"lastName\":\"Huel2\",\"email\":\"sam2@gmail.com\",\"profiles\":[\"User\"]}")
                .put("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"email\":\"sam2@gmail.com\",\"firstName\":\"Sam2\",\"id\":1,\"lastName\":\"Huel2\",\"profiles\":[\"User\"]}"));
    }

    @Test
    void shouldFindAllUsers() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam2\",\"lastName\":\"Huel2\",\"email\":\"sam2@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[{\"email\":\"sam@gmail.com\",\"firstName\":\"Sam\",\"id\":1,\"lastName\":\"Huel\",\"profiles\":[\"Admin\"]},{\"email\":\"sam2@gmail.com\",\"firstName\":\"Sam2\",\"id\":2,\"lastName\":\"Huel2\",\"profiles\":[\"Admin\"]}]"));
    }
    @Test
    void shouldFindAllUsersEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }
}