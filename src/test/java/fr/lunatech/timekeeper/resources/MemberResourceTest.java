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
class MemberResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldAddMemberToProject() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final MemberRequest member = new MemberRequest(1L, Role.Developer);

        final MemberResponse expectedMember = new MemberResponse(4L, 1L, Role.Developer, 3L);
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
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedMember)));
    }

    @Test
    void shouldFindAllMembers() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final MemberRequest member = new MemberRequest(1L, Role.Developer);

        final MemberResponse expectedMember1 = new MemberResponse(4L, 1L, Role.Developer, 3L);
        final MemberResponse expectedMember2 = new MemberResponse(5L, 1L, Role.Developer, 3L);

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
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/5"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<MemberResponse>listOfTasJson(expectedMember1, expectedMember2)));
    }



    @Test
    void shouldFindAllMembersEmpty() {
        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);

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
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(listOfTasJson()));
    }

    @Test
    void shouldNotAddMemberToprojectWithUnknownUser() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":12}")
                .post("/api/members")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownMember() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/members/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldModifyMember() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);

        final MemberRequest member = new MemberRequest(1L, Role.Developer);

        final MemberResponse expectedMember = new MemberResponse(4L, 1L, Role.TeamLeader, 3L);

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
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"" + Role.TeamLeader + "\"}")
                .put("/api/projects/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedMember)));
    }

    @Test
    void shouldRemoveMember() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);

        final MemberRequest member = new MemberRequest(1L, Role.Developer);

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
                .body(member)
                .post("/api/projects/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3/members/4"));

        given()
                .when()
                .delete("/api/projects/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3/members/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when()
                .delete("/api/projects/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}