package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.*;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeEntryDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetPerProjectPerUserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.postValidation;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class TimeEntryResourceTest {

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
    void shouldCreateTimeEntryDay() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy)));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest today = new TimeEntryRequest("Today, I did this test", startDateTime, 8);

        // WHEN I CREATE a timeSheetEntry for TS 2
        create(2L, today, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L,"Today, I did this test", startDateTime, startDateTime.withHour(17).withMinute(0));
        expectedTimeSheetJimmy.entries = List.of(expectedTimeEntry);
        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(expected));
    }

    @Test
    void shouldCreateTimeEntryHalfDay() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy)));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1,9,0);
        TimeEntryRequest morning = new TimeEntryRequest("This morning, I did this test", LocalDateTime.of(2020, 1, 1,9,0), 4);

        // WHEN I CREATE a timeSheetEntry for TS 2
        create(2L, morning, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This morning, I did this test", startDateTime, startDateTime.withHour(13).withMinute(0));
        expectedTimeSheetJimmy.entries = List.of(expectedTimeEntry);
        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(expected));
    }

    @Test
    void shouldCreateTimeEntryHours() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy)));
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2020, 1, 1, 11, 0);


        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test", start, 1);

        // WHEN I CREATE a timeSheetEntry for TS 2
        create(2L, hour, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This hour, I did this test", start, end);
        expectedTimeSheetJimmy.entries = List.of(expectedTimeEntry);
        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(expected));
    }

    @Test
    void shouldFailForNegativeNumberHours() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy)));
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 11, 0);

        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test",  start, -1);

        // WHEN I CREATE a timeSheetEntry for TS 2
        try {
            create(2L, hour, jimmyToken);
        } catch (HttpTestRuntimeException httpError) {
            assertEquals("application/json", httpError.getMimeType());
            assertEquals(400, httpError.getHttpStatus());
        }
    }

    @Test
    void shouldNotCreateTimeEntryForNotOwnerOfTimeSheet() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        final String merryToken = getUser2AccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        create(merryToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy)));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest today = new TimeEntryRequest("Today, I did this test",  startDateTime, 8);

        // THEN
        postValidation(TimeEntryDef.uriWithArgs(2L), merryToken, today, FORBIDDEN);
    }
}