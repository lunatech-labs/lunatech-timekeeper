package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.OrganizationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganizationResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
public class OrganizationResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateOrganizationWhenAdminProfile() {
        final String token = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("France", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final OrganizationResponse expectedorganizationResponse = new OrganizationResponse(1L, "France", "organization.org", List.of(), List.of());
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/organizations/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedorganizationResponse)));
    }
}

