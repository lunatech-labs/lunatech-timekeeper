package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.resources.KeycloakTestResource;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.util.ArrayList;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
public class UserEventTest {

    @Inject
    Flyway flyway;

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;


    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void shouldNotDeleteUserEventIfAssociatedUserIsDeleted() {

        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();
        final var client = create(new ClientRequest("NewClient 2", "Un client créé en tant qu'admin"), adminToken);

        final var project = create(new ProjectRequest("Some Project 2", true, "un projet peut aussi etre créé par un user", client.getId(), true, emptyList()), userAccessToken);

        getValidation(ProjectDef.uriWithid(project.getId()), userAccessToken, OK).body(is(timeKeeperTestUtils.toJson(project)));

        // We use Panache directly to manipulate and test the DB
//        final var organisation = Organization.findAll().firstResult();


//        assertNotNull(organisation);



    }

}
