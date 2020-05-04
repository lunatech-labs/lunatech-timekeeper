package fr.lunatech.timekeeper.resources;

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.resources.utils.ResourceReader;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
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
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.OrganizationDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.UserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceReader.readValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.listOfTasJson;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
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

    //TODO super admin
    @Test
    void shouldCreateOrganizationWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        var __ = create(new OrganizationRequest("MyOrga", "organization.org"), samToken);
        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), samToken);
        readValidation(organization.getId(), OrganizationDef.uri, samToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(organization)));
    }

    //TODO super admin
   /* @Test
    void shouldNotCreateOrganizationWhenUserProfile() {

        final String jimmyToken = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("New Organization", "organization.org");
        given()
                .auth().preemptive().oauth2(jimmyToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }*/

    @Test
    void shouldNotFindUnknownOrganization() {

        final String jimmyToken = getUserAccessToken();

        var __ = create(new OrganizationRequest("MyOrga", "organization.org"), jimmyToken);
        final  Long NO_EXISTING_ORGANIZATION_ID = 243L;

        readValidation(NO_EXISTING_ORGANIZATION_ID, OrganizationDef.uri, jimmyToken)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllOrganizations() {

        final String samToken = getAdminAccessToken();

        final var organization = create(new OrganizationRequest("MyOrga", "organization.org"), samToken);
        final var organization2 = create(new OrganizationRequest("MyOrga2", "organization2.org"), samToken);

        final OrganizationResponse expectedOrganization = new OrganizationResponse(
                organization.getId(),
                "MyOrga",
                "organization.org",
                emptyList(),
                Lists.newArrayList(new OrganizationResponse.OrganizationUserResponse(
                        2L,
                        "Sam Uell",
                        "sam@organization.org",
                        "sam.png",
                        0,
                        Lists.newArrayList(Profile.Admin))
                )
        );

        readValidation(OrganizationDef.uri, samToken)
                .statusCode(OK.getStatusCode())
                .body(is(listOfTasJson(expectedOrganization, organization2)));
    }

    @Test
    void shouldNotFindAllOrganizationsEmpty() {

        final String jimmyToken = getUserAccessToken();

        readValidation(OrganizationDef.uri, jimmyToken)
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldModifyOrganizationWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        final var organization = create(new OrganizationRequest("MyOrga", "organization.org"), samToken);
        update(new OrganizationRequest("MyOrga2", "organization.org"), String.format("%s/%s", OrganizationDef.uri, organization.getId()), samToken);

        final OrganizationResponse expectedOrganization = new OrganizationResponse(
                organization.getId(),
                "MyOrga2",
                "organization.org",
                emptyList(),
                Lists.newArrayList(new OrganizationResponse.OrganizationUserResponse(
                        2L,
                        "Sam Uell",
                        "sam@organization.org",
                        "sam.png",
                        0,
                        Lists.newArrayList(Profile.Admin))
                )
        );

        readValidation(organization.getId(), OrganizationDef.uri, samToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedOrganization)));
    }

    @Test
    void shouldNotModifyOrganizationWhenUserProfile() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), samToken);

        final OrganizationRequest organization2 = new OrganizationRequest("New Organization 2", "organization.org");
        given()
                .auth().preemptive().oauth2(jimmyToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization2)
                .put(String.format("%s/%s", OrganizationDef.uri, organization.getId()))
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}