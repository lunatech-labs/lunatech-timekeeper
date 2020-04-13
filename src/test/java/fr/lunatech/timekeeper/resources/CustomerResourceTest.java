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
    public void testCreateCustomer() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"}"));
    }

    @Test
    public void testCreateCustomerIgnoreUselessParams() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"activitiesId\":[1,2,3],\"id\":9999,\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"}"));
    }
    
    @Test
    public void testGetCustomerNotFound() {
        given()
                .when().get("/api/customers/4")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllCustomers() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient2\",\"description\":\"NewDescription2\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/customers")
                .then()
                .statusCode(200)
                .body(is("[{\"activitiesId\":[],\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\"},{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":2,\"name\":\"NewClient2\"}]"));
    }

    @Test
    public void testGetAllCustomersEmpty() {

        given()
                .when().get("/api/customers")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void testUpdateCustomer(){

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"activitiesId\":[],\"id\":1,\"name\":\"NewName\",\"description\":\"NewDescription2\"}").put("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\"}"));
    }

    @Test
    public void testUpdateCustomerIgnoreUselessParams(){

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"activitiesId\":[1,2,3],\"id\":9999,\"name\":\"NewName\",\"description\":\"NewDescription2\"}").put("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("{\"activitiesId\":[],\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\"}"));
    }

    @Test
    public void testDeleteCustomer() {

        given()
                .when().contentType(MediaType.APPLICATION_JSON).body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}").post("/api/customers")
                .then()
                .statusCode(200);

        given()
                .when().contentType(MediaType.APPLICATION_JSON).delete("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));

        given()
                .when().get("/api/customers/1")
                .then()
                .statusCode(404);

        //idempotent?
        given()
                .when().contentType(MediaType.APPLICATION_JSON).delete("/api/customers/1")
                .then()
                .statusCode(200)
                .body(is("1"));
    }
}