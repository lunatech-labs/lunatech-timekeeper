package fr.lunatech.timekeeper.resources;

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.OrganizationDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.*;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
public class OrganizationResourceTest {

    @Inject
    Flyway flyway;

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

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
        getValidation(OrganizationDef.uriPlusId(organization.getId()), clarkToken, OK).body(is(timeKeeperTestUtils.toJson(organization)));
    }

    @Test
    void shouldNotCreateOrganizationWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("New Organization", "organization.org");
        postValidation(OrganizationDef.uri, samToken, organization, FORBIDDEN);
    }

    @Test
    void shouldNotFindUnknownOrganization() {

        final String clarkToken = getSuperAdminAccessToken();

        final Long NO_EXISTING_ORGANIZATION_ID = 243L;

        getValidation(OrganizationDef.uriPlusId(NO_EXISTING_ORGANIZATION_ID), clarkToken, NOT_FOUND);
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
                        1L,
                        "Clark Kent",
                        "clark@lunatech.fr",
                        "clark.png",
                        0,
                        Lists.newArrayList(Profile.SuperAdmin))
                )
        );

        getValidation(OrganizationDef.uri, clarkToken, OK).body(is(timeKeeperTestUtils.listOfTasJson(lunatechOrganization, organization, organization2)));
    }

    @Test
    void shouldModifyOrganizationWhenAdminProfile() {

        final String clarkToken = getSuperAdminAccessToken();

        final var organization = create(new OrganizationRequest("MyOrga", "organization.org"), clarkToken);
        update(new OrganizationRequest("MyOrga2", "organization.org"), OrganizationDef.uriPlusId(organization.getId()), clarkToken);

        final OrganizationResponse expectedOrganization = new OrganizationResponse(
                organization.getId(),
                "MyOrga2",
                "organization.org",
                emptyList(),
                emptyList()
        );

        getValidation(OrganizationDef.uriPlusId(organization.getId()), clarkToken, OK).body(is(timeKeeperTestUtils.toJson(expectedOrganization)));
    }

    @Test
    void shouldNotModifyOrganizationWhenUserProfile() {

        final String clarkToken = getSuperAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var organization = create(new OrganizationRequest("New Organization", "organization.org"), clarkToken);

        final OrganizationRequest organization2 = new OrganizationRequest("New Organization 2", "organization.org");
        putValidation(OrganizationDef.uriPlusId(organization.getId()), jimmyToken, organization2, FORBIDDEN);
    }
}