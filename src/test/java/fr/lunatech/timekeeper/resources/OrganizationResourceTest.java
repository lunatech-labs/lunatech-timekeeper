package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
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

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ClientDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.OrganizationDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceReader.readValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.listOfTasJson;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
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

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), token);
        readValidation(String.format("%s/%s", OrganizationDef.uri, organization.getId()), token)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(organization)));
    }

    @Test
    void shouldNotCreateOrganizationWhenUserProfile() {

        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("New Organization", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownOrganization() {

        final String token = getUserAccessToken();

        readValidation(String.format("%s/%s", OrganizationDef.uri, 4), token)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllOrganizations() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), adminToken);
        final var organization2 = create(new OrganizationRequest("New Organization 2", "organization.org"), adminToken);

        readValidation(OrganizationDef.uri, token)
                .statusCode(OK.getStatusCode())
                .body(is(listOfTasJson(organization, organization2)));
    }

    @Test
    void shouldFindAllOrganizationsEmpty() {

        final String token = getUserAccessToken();

        readValidation(OrganizationDef.uri, token)
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyOrganizationWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), token);
        update(new OrganizationRequest("New Organization 2", "organization.org"), String.format("%s/%s", OrganizationDef.uri, organization.getId()), token);

        final OrganizationResponse expectedOrganization = new OrganizationResponse(organization.getId(), "New Organization 2", "organization.org", emptyList(), emptyList());
        readValidation(String.format("%s/%s", OrganizationDef.uri, organization.getId()), token)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedOrganization)));
    }

    @Test
    void shouldNotModifyOrganizationWhenUserProfile() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), adminToken);

        final OrganizationRequest organization2 = new OrganizationRequest("New Organization 2", "organization.org");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization2)
                .put(String.format("%s/%s", OrganizationDef.uri, organization.getId()))
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}