package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Role;
import fr.lunatech.timekeeper.resources.utils.ScenarioRunner;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.dtos.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Optional;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ScenarioRunner.*;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.createUserRequest;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.listOfTasJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class PocTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldFindAllProjectsV1() {
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

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 3L, 1L, false);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/4"));

        final ProjectRequest project1 = new ProjectRequest("Pepito", true, "New project", 3L, 1L, false);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project1)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/5"));

        final ProjectResponse expectedProject = new ProjectResponse(4L, "Pepito", true, "New project", "NewClient", emptyList(), 1L, false);
        final ProjectResponse expectedProject1 = new ProjectResponse(5L, "Pepito", true, "New project", "NewClient", emptyList(), 1L, false);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(listOfTasJson(expectedProject, expectedProject1)));
    }

    @Test
    void shouldFindAllProjectsV2() {

        Tuple2<ClientResponse, List<ProjectResponse>> info = distribResource(
                ScenarioRunner.<ClientResponse, ClientRequest>createResource(new ClientRequest("NewClient", "NewDescription"), "/api/clients", ClientResponse.class),
                (ClientResponse clinfo) -> createResource(new ProjectRequest("Pepito", true, "New project", clinfo.getId(), 1L, false), "/api/projects", ProjectResponse.class),
                (ClientResponse clinfo) -> createResource(new ProjectRequest("Pepito", true, "New project", clinfo.getId(), 1L, false), "/api/projects", ProjectResponse.class));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<ProjectResponse>listOfTasJson(info._2.map(f -> f))));

    }

    @Test
    void shouldFindAllMembersV2() {

        var organization = createResource(new OrganizationRequest("NewClient", "organization.org"), "/api/organizations", OrganizationResponse.class);

        ProjectResponse projet = createLinkedResource1(
                createResource(new ClientRequest("NewClient", "NewDescription"), "/api/clients", ClientResponse.class),
                (ClientResponse cliResp) -> createResource(new ProjectRequest("Pepito", true, "New project", cliResp.getId(), organization.getId(), false), "/api/projects", ProjectResponse.class)
        );

        System.out.println("projet.getId() " + projet.getId());
        Tuple2<UserResponse, List<MemberResponse>> resp = distribResource(
                ScenarioRunner.<UserResponse, UserRequest>createResource(createUserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", Admin), "/api/users", UserResponse.class),
                (UserResponse userResp) -> createResource(new MemberRequest(userResp.getId(), Role.Developer), "/api/projects/" + projet.getId() + "/members", MemberResponse.class),
                (UserResponse userResp) -> createResource(new MemberRequest(userResp.getId(), Role.Developer), "/api/projects/" + projet.getId() + "/members", MemberResponse.class)
        );

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/" + projet.getId() + "/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<MemberResponse>listOfTasJson(resp._2)));
    }


}