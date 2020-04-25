package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Role;
import fr.lunatech.timekeeper.services.dtos.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.models.Profile.User;
import static fr.lunatech.timekeeper.resources.TestUtils.*;
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

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserResponse expectedUserResponse = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", listOf(Admin), emptyList());
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedUserResponse)));
    }

    @Test
    void shouldNotCreateUserTwiceWithSameEmail() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam@gmail.com", Admin);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
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
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam@gmail.com", User);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final UserResponse expectedUserResponse = new UserResponse(1L, "Sam2", "Huel2", "sam@gmail.com", listOf(User), emptyList());
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedUserResponse)));
    }

    @Test
    void shouldNotModifyUserEmail() {

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", User);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/1")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    void shouldFindAllUsers() {

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", User);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserResponse expectedUserResponse1 = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", listOf(Admin), emptyList());
        final UserResponse expectedUserResponse2 = new UserResponse(2L, "Sam2", "Huel2", "sam2@gmail.com", listOf(User), emptyList());
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedUserResponse1, expectedUserResponse2)));
    }

    @Test
    void shouldFindAllUsersEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(emptyList())));
    }

    @Test
    void shouldFindUserMemberOfProject() {

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final UserRequest user2 = createUserRequest("Jimmy", "Pastore", "jimmy@gmail.com", User);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/3"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 3L);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        final MemberRequest member1 = new MemberRequest(1L, Role.TeamLeader);
        final MemberRequest member2 = new MemberRequest(2L, Role.Developer);
        final MembersUpdateRequest members = new MembersUpdateRequest(listOf(member1, member2));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(members)
                .put("/api/projects/4/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final MemberResponse expectedMemberResponse1 = new MemberResponse(5L, 1L, Role.TeamLeader, 4L);
        final MemberResponse expectedMemberResponse2 = new MemberResponse(6L, 2L, Role.Developer, 4L);
        final UserResponse expectedUserResponse1 = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", listOf(Admin), listOf(expectedMemberResponse1));
        final UserResponse expectedUserResponse2 = new UserResponse(2L, "Jimmy", "Pastore", "jimmy@gmail.com", listOf(User), listOf(expectedMemberResponse2));
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedUserResponse1, expectedUserResponse2)));
    }
}