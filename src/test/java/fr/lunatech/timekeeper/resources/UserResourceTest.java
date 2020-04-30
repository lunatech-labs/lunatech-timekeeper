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
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
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
@QuarkusTestResource(KeycloakTestResource.class)
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
    void shouldCreateUserWhenAdminProfile() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));



        final UserResponse expectedUserResponse = new UserResponse(2L, "Sam", "Huel", "sam@gmail.com", "sam.png", listOf(Admin), emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedUserResponse)));
    }

    @Test
    void shouldNotCreateUserWhenEmailAlreadyExists() {

        final String token = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    void shouldNotCreateUserWhenUserProfile() {

        final String token = getUserAccessToken();

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownUser() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyUserWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam@gmail.com", "sam2.png", User);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/2")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());



        final UserResponse expectedUserResponse = new UserResponse(2L, "Sam2", "Huel2", "sam@gmail.com", "sam2.png", listOf(User), emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedUserResponse)));
    }

    @Test
    void shouldNotModifyUserEmail() {

        final String token = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", "sam2.png", User);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/2")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    void shouldNotModifyUserWhenUserProfile() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam@gmail.com", "sam2.png", User);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .put("/api/users/2")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldFindAllUsers() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", "sam2.png", User);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/3"));



        final UserResponse expectedUserResponse1 = new UserResponse(2L, "Sam", "Huel", "sam@gmail.com", "sam.png", listOf(Admin), emptyList(),1L);
        final UserResponse expectedUserResponse2 = new UserResponse(3L, "Sam2", "Huel2", "sam2@gmail.com", "sam2.png", listOf(User), emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedUserResponse1, expectedUserResponse2)));
    }

    @Test
    void shouldFindAllUsersEmpty() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(emptyList())));
    }

    @Test
    void shouldFindUserRoleInProject() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = createUserRequest("Jimmy", "Pastore", "jimmy@gmail.com", "jimmy.png", User);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/3"));

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/4"));



        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 4L,1L, false);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/5"));

        final RoleInProjectRequest roleInProject1 = new RoleInProjectRequest(2L, Role.TeamLeader);
        final RoleInProjectRequest roleInProject2 = new RoleInProjectRequest(3L, Role.Developer);
        final RoleInProjectUpdateRequest rolesInProjects = new RoleInProjectUpdateRequest(listOf(roleInProject1, roleInProject2));
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(rolesInProjects)
                .put("/api/projects/5/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());


        final RoleInProjectResponse expectedRoleInProjectResponse1 = new RoleInProjectResponse(6L, 2L, Role.TeamLeader, 5L);
        final RoleInProjectResponse expectedRoleInProjectResponse2 = new RoleInProjectResponse(7L, 3L, Role.Developer, 5L);
        final UserResponse expectedUserResponse1 = new UserResponse(2L, "Sam", "Huel", "sam@gmail.com", "sam.png", listOf(Admin), listOf(expectedRoleInProjectResponse1),1L);
        final UserResponse expectedUserResponse2 = new UserResponse(3L, "Jimmy", "Pastore", "jimmy@gmail.com", "jimmy.png", listOf(User), listOf(expectedRoleInProjectResponse2),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedUserResponse1, expectedUserResponse2)));
    }
}