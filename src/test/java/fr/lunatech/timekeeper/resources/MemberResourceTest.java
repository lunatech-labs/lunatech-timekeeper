package fr.lunatech.timekeeper.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
class MemberResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    public void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testPosMemberResourcesEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}").post("/api/users")
                .then()
                .statusCode(200).body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200).body(is("2"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":2, \"members\":[]}").post("/api/activities")
                .then()
                .statusCode(200).body(is("3"));


        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"role\":\"Developer\", \"userId\":1}").post("/api/activities/3/members")
                .then()
                .statusCode(200).body(is("4"));

        given()
                .when().get("/api/activities/3/members/4")
                .then()
                .statusCode(200)
                .body(is("{\"id\":4,\"role\":\"Developer\",\"userId\":1}"));
    }

    @Test
    public void testPosMemberResourcesWithWrongUserIdEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"role\":\"Developer\", \"userId\":12}").post("/api/members")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetUnExistedMemberResourceEndpoint() {
        given()
                .when().get("/api/members/4")
                .then()
                .statusCode(404);


    }

}