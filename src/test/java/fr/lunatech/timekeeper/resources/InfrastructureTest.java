package fr.lunatech.timekeeper.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class InfrastructureTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldPingOpenApi() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/openapi")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingSwaggerUI() {
        given()
                .when()
                .header(ACCEPT, TEXT_HTML)
                .get("/swagger-ui/")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingHealth() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/health")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingLiveness() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/health/live")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingReadiness() {
        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/health/ready")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldRetrieveTokensFromKeycloak() {
        Assert.assertNotNull(KeycloakTestResource.getUserAccessToken());
        Assert.assertNotNull(KeycloakTestResource.getAdminAccessToken());
    }
}