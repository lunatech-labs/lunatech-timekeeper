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
class ActivityResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    public void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testPostActivityResourcesEndpoint() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200).body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":1, \"members\":[]}").post("/api/activities")
                .then()
                .statusCode(200).body(is("2"));

        given()
                .when().get("/api/activities/2")
                .then()
                .statusCode(200)
                .body(is("{\"billable\":true,\"customerId\":1,\"description\":\"New project\",\"name\":\"Pepito\",\"id\":2,\"membersId\":[]}"));
    }

    @Test
    public void testPostActivityResourcesWithMemberValidEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\", \"profiles\":[\"Admin\"]}").post("/api/users")
                .then()
                .statusCode(200).body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200).body(is("2"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"Pepito\",\"billable\":\"true\",\"description\":\"New project\",\"customerId\":2}").post("/api/activities")
                .then()
                .statusCode(200).body(is("3"));

        given()
                .when().get("/api/activities/3")
                .then()
                .statusCode(200)
                .body(is("{\"billable\":true,\"customerId\":2,\"description\":\"New project\",\"name\":\"Pepito\",\"id\":3,\"membersId\":[]}"));
    }

    @Test
    public void testPostActivityResourcesWithWrongCustomerIdEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"Pepito\",\"billable\":true,\"description\":\"New project\", \"customerId\":10, \"members\":[]}").post("/api/activities")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetUnExistedActivityResourceEndpoint() {
        given()
                .when().get("/api/activities/4")
                .then()
                .statusCode(404);
    }

}