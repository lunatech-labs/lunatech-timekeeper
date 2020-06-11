package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.PublicHoliday;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.PersonalTimeSheetsDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
public class PersonalTimesheetsResourceTest {

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldReturnEmptyWeekResponseIfUserNotMemberOfProject() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);

        final var client = create(new ClientRequest("Disney", "The Disneyland company"), adminToken);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(jimmyProjectRequest);
        final var project = create(new ProjectRequest("Disney Content API", true, "Project for the backend system", client.getId(), true, newUsers), adminToken);

        WeekResponse response = new WeekResponse(LocalDate.of(2020,5,25), Collections.emptyList(),Collections.emptyList(),Collections.emptyList());

        getValidation(PersonalTimeSheetsDef.uriWithMultiInt(2020,22), adminToken, OK).body(is(timeKeeperTestUtils.toJson(response)));
    }

    @Test
    void shouldReturnSomeWeekResponseForProjectMember() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        UserResponse jimmy = create(jimmyToken);

        final var client = create(new ClientRequest("Disney", "The Disneyland company"), adminToken);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(jimmyProjectRequest);
        final var project = create(new ProjectRequest("Disney Content API", true, "Project for the backend system", client.getId(), true, newUsers), adminToken);

        final List<ProjectResponse.ProjectUserResponse> expectedProjectUsers = List.of(
                new ProjectResponse.ProjectUserResponse(jimmy.getId()
                        , false
                        , jimmy.getName()
                        , jimmy.getPicture()
                )
        );
        final var projectResponse = new ProjectResponse(project.getId()
                , project.getName()
                , project.isBillable()
                , project.getDescription()
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , expectedProjectUsers
                , project.isPublicAccess());


        final var timesheet = new TimeSheetResponse(1L, projectResponse, jimmy, TimeUnit.HOURLY, true,null,null, "HOURLY", Collections.emptyList());
        final List<PublicHoliday> holidays = new ArrayList<>();
        holidays.add(new PublicHoliday(LocalDate.of(2020,05,21), "Jour de l'Ascension","Ascension Day","FR"));
        WeekResponse response = new WeekResponse(LocalDate.of(2020,5,18), Collections.emptyList(), List.of(timesheet), holidays);

        getValidation(PersonalTimeSheetsDef.uriWithMultiInt(2020,21), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(response)));
    }

}
