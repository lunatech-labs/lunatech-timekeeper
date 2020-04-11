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
class UserResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    public void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testPostUserResourcesEndpoint() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"email\":\"sam@gmail.com\",\"profiles\":[\"Admin\"]}").post("/api/users")
                .then()
                .statusCode(200).body(is("1"));

        given()
                .when().get("/api/users/1")
                .then()
                .statusCode(200)
                .body(is("{\"email\":\"sam@gmail.com\",\"firstName\":\"Sam\",\"lastName\":\"Huel\",\"profiles\":[\"Admin\"],\"id\":1}"));
    }

    @Test
    public void testGetUnExistedUserResourceEndpoint() {
        given()
                .when().get("/api/users/4")
                .then()
                .statusCode(404);


    }


}