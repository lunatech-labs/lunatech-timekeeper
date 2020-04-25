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
class ProjectResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateProject() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L);
        final ProjectResponse expectedProject = new ProjectResponse(2L, "Pepito", true, "New project", "NewClient", emptyList());

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
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/2"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldCreateProjectWithUnknownClient() {

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 10L);
        final ProjectResponse expectedProject = new ProjectResponse(1L, "Pepito", true, "New project", "", emptyList());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldNotFindUnknownProject() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllProjects() {
        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final ProjectRequest project1 = new ProjectRequest("Pepito", true, "New project", 2L);

        final ProjectResponse expectedProject = new ProjectResponse(3L, "Pepito", true, "New project", "NewClient", emptyList());
        final ProjectResponse expectedProject1 = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", emptyList());

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
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project1)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedProject, expectedProject1)));
    }

    @Test
    void shouldFindAllProjectsEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyProject() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ClientRequest client1 = new ClientRequest("NewClient2", "NewDescription2");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L);
        final ProjectRequest project1 = new ProjectRequest("Pepito2", false, "New project2", 3L);

        final ProjectResponse expectedProject = new ProjectResponse(2L, "Pepito2", false, "New project2", "NewClient2", emptyList());

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
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client1)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project1)
                .put("/api/projects/2")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldAddMemberToProject() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        final MemberRequest member = new MemberRequest(1L, Role.Developer);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        final MemberResponse expectedMemberResponse = new MemberResponse(4L, 1L, Role.Developer, 3L);
        final ProjectResponse expectedProject = new ProjectResponse(3L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse));
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldNotAddMemberTwiceToProject() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        final MemberRequest member = new MemberRequest(1L, Role.Developer);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        final MemberRequest member2 = new MemberRequest(1L, Role.TeamLeader);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(member2)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));
    }

    @Test
    void shouldNotAddMemberToProjectWithUnknownUser() {

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":12}")
                .post("/api/projects/2/members")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldModifyMembersAndGetProjectMembers() {

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
        final ProjectResponse expectedProject = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse1, expectedMemberResponse2));
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));

        final UserRequest user2_1 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", Admin);
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2_1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/7"));

        final MemberRequest member2_1 = new MemberRequest(7L, Role.TeamLeader);
        final MembersUpdateRequest members2 = new MembersUpdateRequest(listOf(member2_1));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(members2)
                .put("/api/projects/4/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final MemberResponse expectedMemberResponse2_1 = new MemberResponse(8L, 7L, Role.TeamLeader, 4L);
        final ProjectResponse expectedProject2 = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse2_1));
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject2)));
    }
}