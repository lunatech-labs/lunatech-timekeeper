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
    public void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testPostCustomerResourcesWithOutActivitiesEndpoint() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"name\":\"NewClient\",\"activitiesId\":[],\"id\":1}"));
    }

    @Test
    public void testGetUnExistedCustomerResourceEndpoint() {
        given()
                .when().get("/api/customers/4")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllCustomersWithOutActivitiesEndpoint() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient2\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/customers")
                .then()
                .statusCode(200)
                .body(is("[{\"name\":\"NewClient\",\"activitiesId\":[],\"id\":1},{\"name\":\"NewClient2\",\"activitiesId\":[],\"id\":2}]"));
    }

    @Test
    public void testGetAllCustomersWithOutCustomerAndWithOutActivitiesEndpoint() {

        given()
                .when().get("/api/customers")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void testUpdateCustomerWithOutActivitiesEndpoint(){

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"activitiesId\":[],\"id\":1,\"name\":\"NewName\"}").put("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"name\":\"NewName\",\"activitiesId\":[],\"id\":1}"));
    }

   /* @Test
    public void testDeleteCustomerEndpoint() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"activitiesId\":[],\"id\":1,\"name\":\"NewName\"}").delete("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(404);
    }*/
}