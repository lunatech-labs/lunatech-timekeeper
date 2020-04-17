package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.CustomerRequest;
import fr.lunatech.timekeeper.services.dtos.CustomerResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;

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
class CustomerResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateCustomer() {

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final CustomerResponse expectedCustomer = new CustomerResponse(1L, "NewClient", "NewDescription", new ArrayList<Long>());

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
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedCustomer)));
    }

    @Test
    void shouldCreateCustomerIgnoreUselessParams() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"projectsId\":[1,2,3],\"id\":9999,\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"description\":\"NewDescription\",\"id\":1,\"name\":\"NewClient\",\"projectId\":[]}"));
    }

    @Test
    void shouldNotFindUnknownCustomer() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/4")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllCustomers() {

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final CustomerRequest customer2 = new CustomerRequest("NewClient2", "NewDescription2");
        final CustomerResponse expectedCustomer = new CustomerResponse(1L, "NewClient", "NewDescription", new ArrayList<Long>());
        final CustomerResponse expectedCustomer2 = new CustomerResponse(2L, "NewClient2", "NewDescription2", new ArrayList<Long>());

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
                .body(customer2)
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/2"));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.<CustomerResponse>listOfTasJson(expectedCustomer, expectedCustomer2)));
    }

    @Test
    void shouldFindAllCustomersEmpty() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyCustomer() {

        final CustomerRequest customer = new CustomerRequest("NewClient", "NewDescription");
        final CustomerRequest customer2 = new CustomerRequest("NewClient", "NewDescription2");

        final CustomerResponse expectedCustomer2 = new CustomerResponse(1L, "NewClient", "NewDescription2", new ArrayList<Long>());

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
                .body(customer2)
                .put("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedCustomer2)));
    }

    @Test
    void shouldModifyCustomerIgnoreUselessParams() {

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"name\":\"NewClient\",\"description\":\"NewDescription\"}")
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/customers/1"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body("{\"projectsId\":[1,2,3],\"id\":9999,\"name\":\"NewName\",\"description\":\"NewDescription2\"}")
                .put("/api/customers/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is("{\"description\":\"NewDescription2\",\"id\":1,\"name\":\"NewName\",\"projectId\":[]}"));
    }
}