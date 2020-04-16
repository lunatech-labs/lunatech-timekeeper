package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.ActivityRequest;
import fr.lunatech.timekeeper.services.dtos.ActivityResponse;
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
class ActivityResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateActivity() {

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final ActivityRequest activity = new ActivityRequest("Pepito", true, "New project", 1L);

        final ActivityResponse expectedActivity = new ActivityResponse(2L, "Pepito", true, "New project", 1L, new ArrayList<Long>());

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
                .body(activity)
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/2"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedActivity)));
    }

    @Test
    void shouldNotCreateActivityWithUnknownCustomer() {

        final ActivityRequest activity = new ActivityRequest("Pepito", true, "New project", 10L);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(activity)
                .post("/api/activities")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownActivity() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllActivities() {
        final UserRequest user = createUserRequest("Sam", "Huel", "sam@gmail.com", Admin);
        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final ActivityRequest activity = new ActivityRequest("Pepito", true, "New project", 2L);
        final ActivityRequest activity1 = new ActivityRequest("Pepito", true, "New project", 2L);

        final ActivityResponse expectedActivity = new ActivityResponse(3L, "Pepito", true, "New project", 2L, new ArrayList<Long>());
        final ActivityResponse expectedActivity1 = new ActivityResponse(4L, "Pepito", true, "New project", 2L, new ArrayList<Long>());

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
                .body(activity)
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(activity1)
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/4"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<ActivityResponse>listOfTasJson(expectedActivity, expectedActivity1)));
    }

    @Test
    void shouldFindAllActivitiesEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyActivity() {

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final CustomerRequest customer1 = new CustomerRequest("NewClient2", "NewDescription2");
        final ActivityRequest activity = new ActivityRequest("Pepito", true, "New project", 1L);
        final ActivityRequest activity1 = new ActivityRequest("Pepito2", false, "New project2", 3L);

        final ActivityResponse expectedActivity = new ActivityResponse(2L, "Pepito2", false, "New project2", 3L, new ArrayList<Long>());

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
                .body(activity)
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/2"));

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
                .body(activity1)
                .put("/api/activities/2")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedActivity)));
    }
}