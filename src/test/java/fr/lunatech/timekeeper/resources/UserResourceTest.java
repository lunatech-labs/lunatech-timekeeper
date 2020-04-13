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
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header("Location", endsWith("/api/users/1"));

        given()
                .when()
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"email\":\"sam@gmail.com\",\"firstName\":\"Sam\",\"id\":1,\"lastName\":\"Huel\",\"profiles\":[\"Admin\"]}"));
    }

    @Test
    void shouldNotFindUnknownUser() {
        given()
                .when()
                .get("/api/users/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyUser() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header("Location", endsWith("/api/users/1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"firstName\":\"Sam2\",\"lastName\":\"Huel2\",\"email\":\"sam2@gmail.com\",\"profiles\":[\"SimpleUSer\"]}")
                .put("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when().get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"email\":\"sam2@gmail.com\",\"firstName\":\"Sam2\",\"id\":1,\"lastName\":\"Huel2\",\"profiles\":[\"SimpleUSer\"]}"));
    }

    @Test
    void shouldRemoveUser() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header("Location", endsWith("/api/users/1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .get("/api/users/1")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .delete("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}