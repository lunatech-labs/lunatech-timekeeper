package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerDayRequest;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetPerProjectPerUserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;

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
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy, TimeUnit.HOURLY, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(timeKeeperTestUtils.listOfTasJson(expectedTimeSheetJimmy)));
        LocalDateTime date = LocalDateTime.of(2020, 1, 1, 15, 0);
        TimeEntryPerDayRequest today = new TimeEntryPerDayRequest("Today, I did this test", true, date);

        // WHEN I CREATE a timeSheetEntry for TS 2
        create(2L, today, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, true, "Today, I did this test", date.withHour(9).withMinute(0), date.withHour(17).withMinute(0));
        expectedTimeSheetJimmy.entries = List.of(expectedTimeEntry);
        List<TimeSheetResponse> ts = Arrays.<TimeSheetResponse>asList(expectedTimeSheetJimmy);
        var expected = timeKeeperTestUtils.toJson(ts);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken, OK).body(is(expected));
    }
}