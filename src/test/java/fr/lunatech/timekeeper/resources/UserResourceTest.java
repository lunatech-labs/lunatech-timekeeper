package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.models.Profile.User;
import static fr.lunatech.timekeeper.resources.TestUtils.*;
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

        final List<Profile> profilesExpected = new ArrayList<>();
        profilesExpected.add(Admin);

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final UserResponse expectedUserResponse = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", profilesExpected, new ArrayList<Long>());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
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
                .body(is(toJson(expectedUserResponse)));
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

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", User);

        final List<Profile> profileExpected = new ArrayList<>();
        profileExpected.add(User);

        final UserResponse expectedUserResponse = new UserResponse(1L, "Sam2", "Huel2", "sam2@gmail.com", profileExpected, new ArrayList<Long>());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedUserResponse)));
    }

    @Test
    void shouldFindAllUsers() {

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", User);

        final List<Profile> profileExpected1 = new ArrayList<>();
        profileExpected1.add(Admin);
        final List<Profile> profileExpected2 = new ArrayList<>();
        profileExpected2.add(User);

        final UserResponse expectedUserResponse1 = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", profileExpected1, new ArrayList<Long>());
        final UserResponse expectedUserResponse2 = new UserResponse(2L, "Sam2", "Huel2", "sam2@gmail.com", profileExpected2, new ArrayList<Long>());



        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
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
                .body(is(TestUtils.<UserResponse>listOfTasJson(expectedUserResponse1,expectedUserResponse2)));
    }

    @Test
    void shouldFindAllUsersEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(new ArrayList<UserResponse>())));
    }
}