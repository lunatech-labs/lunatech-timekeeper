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
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"firstname\":\"Sam\",\"lastname\":\"Huel\",\"email\":\"sam@gmail.com\", \"profile\":\"Admin\"}").post("/users")
                .then()
                .statusCode(200).body(is("1"));

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"role\":\"Developer\", \"userId\":1}").post("/members")
                .then()
                .statusCode(200).body(is("2"));

        given()
                .when().get("/members/2")
                .then()
                .statusCode(200)
                .body(is("{\"id\":2,\"role\":\"Developer\",\"userId\":1}"));
    }

    @Test
    public void testPosMemberResourcesWithWrongUserIdEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"role\":\"Developer\", \"userId\":12}").post("/members")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetUnExistedMemberResourceEndpoint() {
        given()
                .when().get("/members/4")
                .then()
                .statusCode(404);


    }


}