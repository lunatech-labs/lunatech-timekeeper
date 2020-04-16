package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Profile;
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

import java.util.ArrayList;
import java.util.List;

import static fr.lunatech.timekeeper.models.Profile.Admin;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static fr.lunatech.timekeeper.resources.TestUtils.toJson;

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
    void shouldAddMemberToActivity() {

        final List<Profile> profiles = new ArrayList<>();
        profiles.add(Admin);
        final UserRequest user = new UserRequest("Sam", "Huel", "sam@gmail.com", profiles);

        final UserResponse expectedUserResponse = new UserResponse(1L, "Sam", "Huel", "sam@gmail.com", profiles, new ArrayList<Long>());

        final CustomerRequest customer = new CustomerRequest("NewClient","NewDescription");
        final ActivityRequest activity = new ActivityRequest("Pepito",true,"New project",2L);
        final MemberRequest member = new MemberRequest(1L,Role.Developer);

        final MemberResponse expectedMember = new MemberResponse(4L,1L,Role.Developer);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(toJson(user))
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(toJson(customer))
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(toJson(activity))
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(toJson(member))
                .post("/api/activities/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3/members/4"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedMember)));
    }


    @Test
    void shouldFindAllMembers() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3/members/4"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3/members/5"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[{\"id\":4,\"role\":\"Developer\",\"userId\":1},{\"id\":5,\"role\":\"Developer\",\"userId\":1}]"));
    }

    @Test
    void shouldFindAllMembersEmpty() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/3/members")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldNotAddMemberToActivityWithUnknownUser() {
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
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3/members/4"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"TeamLeader\"}")
                .put("/api/activities/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/3/members/4")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"id\":4,\"role\":\"TeamLeader\",\"userId\":1}"));
    }

    @Test
    void shouldRemoveMember() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}")
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}")
                .post("/api/activities")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"role\":\"Developer\", \"userId\":1}")
                .post("/api/activities/3/members")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/activities/3/members/4"));

        given()
                .when()
                .delete("/api/activities/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/activities/3/members/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //idempotent?
        given()
                .when()
                .delete("/api/activities/3/members/4")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}