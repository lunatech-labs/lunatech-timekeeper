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
class ProjectResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateProjectWhenAdminProfile() {

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

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L, 1L);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        final ProjectResponse expectedProject = new ProjectResponse(3L, "Pepito", true, "New project", "NewClient", emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldCreateProjectWithUnknownClient() {

        final String token = getUserAccessToken();

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

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 10L,1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/2"));

        final ProjectResponse expectedProject = new ProjectResponse(2L, "Pepito", true, "New project", "", emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldNotFindUnknownProject() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllProjects() {

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

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/3"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 3L, 1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        final ProjectRequest project1 = new ProjectRequest("Pepito", true, "New project", 3L,1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project1)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/5"));

        final ProjectResponse expectedProject = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", emptyList(),1L);
        final ProjectResponse expectedProject1 = new ProjectResponse(5L, "Pepito", true, "New project", "NewClient", emptyList(),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedProject, expectedProject1)));
    }

    @Test
    void shouldFindAllProjectsEmpty() {

        final String token = getUserAccessToken();

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyProject() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "organization.org");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/2"));

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L, 2L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        final ClientRequest client1 = new ClientRequest("NewClient2", "NewDescription2");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client1)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/4"));

        final ProjectRequest project1 = new ProjectRequest("Pepito2", false, "New project2", 4L, 2L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project1)
                .put("/api/projects/3")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final ProjectResponse expectedProject = new ProjectResponse(3L, "Pepito2", false, "New project2", "NewClient2", emptyList(),2L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldAddMemberToProject() {

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

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/3"));


        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 3L,1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        final MemberRequest member = new MemberRequest(2L, Role.Developer);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(member)
                .post("/api/projects/4/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4/members/5"));

        final MemberResponse expectedMemberResponse = new MemberResponse(5L, 2L, Role.Developer, 4L);
        final ProjectResponse expectedProject = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));
    }

    @Test
    void shouldNotAddMemberTwiceToProject() {

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

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/3"));



        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 3L, 1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        final MemberRequest member = new MemberRequest(2L, Role.Developer);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(member)
                .post("/api/projects/4/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4/members/5"));

        final MemberRequest member2 = new MemberRequest(2L, Role.TeamLeader);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(member2)
                .post("/api/projects/4/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4/members/5"));
    }

    @Test
    void shouldNotAddMemberToProjectWithUnknownUser() {

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

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/2"));



        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L,1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/3"));

        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":12}")
                .post("/api/projects/2/members")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldModifyMembersAndGetProjectMembers() {

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



        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 4L, 1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/5"));

        final MemberRequest member1 = new MemberRequest(2L, Role.TeamLeader);
        final MemberRequest member2 = new MemberRequest(3L, Role.Developer);
        final MembersUpdateRequest members = new MembersUpdateRequest(listOf(member1, member2));
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(members)
                .put("/api/projects/5/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final MemberResponse expectedMemberResponse1 = new MemberResponse(6L, 2L, Role.TeamLeader, 5L);
        final MemberResponse expectedMemberResponse2 = new MemberResponse(7L, 3L, Role.Developer, 5L);
        final ProjectResponse expectedProject = new ProjectResponse(5L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse1, expectedMemberResponse2),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/5")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject)));

        final UserRequest user2_1 = createUserRequest("Sam2", "Huel2", "sam2@gmail.com", "sam2.png", Admin);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2_1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/8"));

        final MemberRequest member2_1 = new MemberRequest(8L, Role.TeamLeader);
        final MembersUpdateRequest members2 = new MembersUpdateRequest(listOf(member2_1));
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(members2)
                .put("/api/projects/5/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        final MemberResponse expectedMemberResponse2_1 = new MemberResponse(9L, 8L, Role.TeamLeader, 5L);
        final ProjectResponse expectedProject2 = new ProjectResponse(5L, "Pepito", true, "New project", "NewClient", listOf(expectedMemberResponse2_1),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/5")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedProject2)));
    }
}