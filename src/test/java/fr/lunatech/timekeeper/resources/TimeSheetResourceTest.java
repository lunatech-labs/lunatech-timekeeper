package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.putValidation;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.core.Is.is;


@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class TimeSheetResourceTest {

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
    void shouldFindTimeSheetById() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);


        final var expectedTimeSheetSam = new TimeSheetResponse(1L, project, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null);
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null);

        // THEN
        getValidation(TimeSheetDef.uriPlusId(1L), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));
        getValidation(TimeSheetDef.uriPlusId(2L), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldUpdateTimeSheetById() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        var sam = create(adminToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        Long timeSheetId = 1L;

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);
        // verify first version
        final var expectedTimeSheetSam = new TimeSheetResponse(timeSheetId, project, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));

        // WHEN : AND the timesheet is updated (adding a end date a maxDuration and changing units)
        LocalDate newEndDate = LocalDate.now().plusMonths(2L);
        TimeSheetRequest updatedTimeSheet = new TimeSheetRequest(
                TimeUnit.DAY,
                true,
                newEndDate,
                60,
                TimeUnit.DAY
        );
        putValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken, timeKeeperTestUtils.toJson(updatedTimeSheet)).statusCode(NO_CONTENT.getStatusCode());

        // THEN get the updated version
        final var expectedUpdatedTimeSheetSam = new TimeSheetResponse(timeSheetId, project, sam.getId(), TimeUnit.DAY, true, newEndDate, 60, TimeUnit.DAY.toString(), Collections.emptyList(), 60L);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(timeKeeperTestUtils.toJson(expectedUpdatedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

}