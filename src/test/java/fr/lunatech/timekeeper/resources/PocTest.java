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
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.resources.utils.ScenarioRunner.*;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.createUserRequest;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
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
        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final ProjectRequest project1 = new ProjectRequest("Pepito", true, "New project", 2L);

        final ProjectResponse expectedProject = new ProjectResponse(3L, "Pepito", true, "New project", 2L, new ArrayList<Long>());
        final ProjectResponse expectedProject1 = new ProjectResponse(4L, "Pepito", true, "New project", 2L, new ArrayList<Long>());

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
                .body(is(TestUtils.<ProjectResponse>listOfTasJson(expectedProject, expectedProject1)));
    }

    @Test
    void shouldFindAllProjectsV2() {

        Tuple2<ClientResponse, List<ProjectResponse>> info = distribResource(
                ScenarioRunner.<ClientResponse, ClientRequest>createResource(new ClientRequest("NewClient", "NewDescription"), "/api/clients", ClientResponse.class),
                (ClientResponse clinfo) -> createResource(new ProjectRequest("Pepito", true, "New project", clinfo.getId()), "/api/projects", ProjectResponse.class),
                (ClientResponse clinfo) -> createResource(new ProjectRequest("Pepito", true, "New project", clinfo.getId()), "/api/projects", ProjectResponse.class));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<ProjectResponse>listOfTasJson(info._2.map(f -> f))));

    }

    @Test
    void shouldFindAllMembersV1() {

        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final MemberRequest member = new MemberRequest(1L, Role.Developer);

        final MemberResponse expectedMember1 = new MemberResponse(4L, 1L, Role.Developer);
        final MemberResponse expectedMember2 = new MemberResponse(5L, 1L, Role.Developer);

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
    void shouldFindAllMembersV2() {

        final MemberRequest member = new MemberRequest(1L, Role.Developer);

        final MemberResponse expectedMember1 = new MemberResponse(4L, 1L, Role.Developer);
        final MemberResponse expectedMember2 = new MemberResponse(5L, 1L, Role.Developer);

        ProjectResponse projet = createLinkedResource1(
                createResource(new ClientRequest("NewClient", "NewDescription"), "/api/clients", ClientResponse.class),
                (ClientResponse cliResp) -> createResource(new ProjectRequest("Pepito", true, "New project", cliResp.getId()), "/api/projects", ProjectResponse.class)

        );

        System.out.println("projet.getId() " + projet.getId());
        Tuple2<UserResponse, List<MemberResponse>> resp = distribResource(
                ScenarioRunner.<UserResponse, UserRequest>createResource(createUserRequest("Sam", "Huel", "sam@gmail.com", Admin), "/api/users", UserResponse.class),
                (UserResponse userResp) -> createResource(new MemberRequest(userResp.getId(), Role.Developer), "/api/projects/" + projet.getId() + "/members", MemberResponse.class),
                (UserResponse userResp) -> createResource(new MemberRequest(userResp.getId(), Role.Developer), "/api/projects/" + projet.getId() + "/members", MemberResponse.class)
        );

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects/"+projet.getId()+"/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<MemberResponse>listOfTasJson(resp._2)));
    }


}