package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.services.dtos.CustomerRequest;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static fr.lunatech.timekeeper.resources.TestUtils.createUserRequest;
import static fr.lunatech.timekeeper.resources.TestUtils.toJson;
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

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L);

        final ProjectResponse expectedproject = new ProjectResponse(2L, "Pepito", true, "New project", 1L, new ArrayList<Long>());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(customer)
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

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
                .body(is(toJson(expectedproject)));
    }

    @Test
    void shouldNotCreateProjectWithUnknownCustomer() {

        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 10L);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
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
        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 2L);
        final ProjectRequest project1 = new ProjectRequest("Pepito", true, "New project", 2L);

        final ProjectResponse expectedproject = new ProjectResponse(3L, "Pepito", true, "New project", 2L, new ArrayList<Long>());
        final ProjectResponse expectedproject1 = new ProjectResponse(4L, "Pepito", true, "New project", 2L, new ArrayList<Long>());

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
                .body(customer)
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

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
                .body(is(TestUtils.<ProjectResponse>listOfTasJson(expectedproject, expectedproject1)));
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

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final CustomerRequest customer1 = new CustomerRequest("NewClient2", "NewDescription2");
        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 1L);
        final ProjectRequest project1 = new ProjectRequest("Pepito2", false, "New project2", 3L);

        final ProjectResponse expectedproject = new ProjectResponse(2L, "Pepito2", false, "New project2", 3L, new ArrayList<Long>());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(customer)
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

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
                .body(customer1)
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/3"));

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
                .body(is(toJson(expectedproject)));
    }
}