package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
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
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.putValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
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

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldFindTimeSheetById(){
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
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);


        final var expectedTimeSheetSam = new TimeSheetResponse(8L, project, sam, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());
        final var expectedTimeSheetJimmy = new TimeSheetResponse(9L, project, jimmy, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());

        // THEN
        getValidation(TimeSheetDef.uriWithid(8L),adminToken, OK).body(is(toJson(expectedTimeSheetSam)));
        getValidation(TimeSheetDef.uriWithid(9L),adminToken, OK).body(is(toJson(expectedTimeSheetJimmy)));

    }


    @Test
    void shouldUpdateTimeSheetById(){
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        var sam = create(adminToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        // FIXME: see TK-359
        Long expectedId = 6L;

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);
        // verify first version
        final var expectedTimeSheetSam = new TimeSheetResponse(expectedId, project, sam, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());
        getValidation(TimeSheetDef.uriWithid(expectedId), adminToken, OK).body(is(toJson(expectedTimeSheetSam)));

        // WHEN : AND the timesheet is updated (adding a end date a maxDuration and changing units)
        LocalDate newEndDate = LocalDate.now().plusMonths(2L);
        TimeSheetRequest updatedTimeSheet = new TimeSheetRequest(
                TimeUnit.DAY,
                true,
                newEndDate,
                60,
                TimeUnit.DAY
        );
        // in order to avoid Jackson seralization by rest-assured, serialise now
        putValidation(TimeSheetDef.uriWithid(expectedId),adminToken,toJson(updatedTimeSheet), NO_CONTENT);

        // THEN get the updated version
        final var expectedUpdatedTimeSheetSam = new TimeSheetResponse(expectedId, project, sam, true, newEndDate, 60, TimeUnit.DAY.toString(), Collections.emptyList());
        getValidation(TimeSheetDef.uriWithid(expectedId), adminToken, OK).body(is(toJson(expectedUpdatedTimeSheetSam)));
    }

}