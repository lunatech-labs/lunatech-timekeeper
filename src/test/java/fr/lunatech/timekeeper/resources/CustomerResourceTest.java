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
class CustomerResourceTest {


    @Inject
    Flyway flyway;

    @AfterEach
    public void cleanDB(){
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testPostCustomerResourcesWithOutActivitiesEndpoint() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"id\":1,\"activities\":[],\"name\":\"NewClient\"}"));
    }


}