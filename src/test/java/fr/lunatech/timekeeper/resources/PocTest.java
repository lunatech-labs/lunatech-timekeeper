package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.ResourceFactory;
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

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ScenarioRunner.distribResource;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.*;
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
    void shouldCreateClientWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/1"));

        final ClientResponse expectedClient = new ClientResponse(1L, "NewClient", "NewDescription", emptyList());
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

   @Test
    void shouldCreateClientWhenAdminProfileV2() {

        final String token = getAdminAccessToken();

        var clientResponse = create(new ClientRequest("NewClient", "NewDescription"),token);


        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/clients/"+clientResponse.getId())
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(clientResponse)));
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
        final String adminToken = getAdminAccessToken();

        OrganizationResponse organization = ResourceFactory.create(new OrganizationRequest("NewClient", "organization.org"),adminToken);

        Tuple2<ClientResponse, List<ProjectResponse>> info = distribResource(
                create(new ClientRequest("NewClient", "NewDescription"),adminToken),
                (ClientResponse clinfo) -> create(new ProjectRequest("Pepito", true, "New project", clinfo.getId(), organization.getId(), false),adminToken),
                (ClientResponse clinfo) -> create(new ProjectRequest("Pepito", true, "New project", clinfo.getId(), organization.getId(), false),adminToken));

        given().auth().preemptive().oauth2(getAdminAccessToken())
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/projects")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<ProjectResponse>listOfTasJson(info._2.map(f -> f))));
    }




}