package fr.lunatech.timekeeper.resources;

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.OrganizationDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceReader.readValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.listOfTasJson;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
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
    void shouldCreateOrganizationWhenSuperAdminProfile() {

        final String clarkToken = getSuperAdminAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), clarkToken);
        readValidation(organization.getId(), OrganizationDef.uri, clarkToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(organization)));
    }


    @Test
    void shouldNotCreateOrganizationWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("New Organization", "organization.org");
        given()
                .auth().preemptive().oauth2(samToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownOrganization() {

        final String clarkToken = getSuperAdminAccessToken();

        final  Long NO_EXISTING_ORGANIZATION_ID = 243L;

        readValidation(NO_EXISTING_ORGANIZATION_ID, OrganizationDef.uri, clarkToken)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllOrganizations() {

        final String clarkToken = getSuperAdminAccessToken();

        final var organization = create(new OrganizationRequest("MyOrga", "organization.org"), clarkToken);
        final var organization2 = create(new OrganizationRequest("MyOrga2", "organization2.org"), clarkToken);

        final OrganizationResponse lunatechOrganization = new OrganizationResponse(
                1L,
                "Lunatech FR",
                "lunatech.fr",
                emptyList(),
                Lists.newArrayList(new OrganizationResponse.OrganizationUserResponse(
                        2L,
                        "Clark Kent",
                        "clark@lunatech.fr",
                        "clark.png",
                        0,
                        Lists.newArrayList(Profile.SuperAdmin))
                )
        );

        readValidation(OrganizationDef.uri, clarkToken)
                .statusCode(OK.getStatusCode())
                .body(is(listOfTasJson(lunatechOrganization, organization, organization2)));
    }

    @Test
    void shouldModifyOrganizationWhenAdminProfile() {

        final String clarkToken = getSuperAdminAccessToken();

        final var organization = create(new OrganizationRequest("MyOrga", "organization.org"), clarkToken);
        update(new OrganizationRequest("MyOrga2", "organization.org"), String.format("%s/%s", OrganizationDef.uri, organization.getId()), clarkToken);

        final OrganizationResponse expectedOrganization = new OrganizationResponse(
                organization.getId(),
                "MyOrga2",
                "organization.org",
                emptyList(),
                emptyList()
        );

        readValidation(organization.getId(), OrganizationDef.uri, clarkToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedOrganization)));
    }

    @Test
    void shouldNotModifyOrganizationWhenUserProfile() {

        final String clarkToken = getSuperAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), clarkToken);

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