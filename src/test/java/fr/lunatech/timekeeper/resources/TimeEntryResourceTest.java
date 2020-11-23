/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.postValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.deleteValidation;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest

@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
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

    final LocalDate START_DATE = LocalDate.now();

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

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest today = new TimeEntryRequest("Today, I did this test", startDateTime, 8);

        create(2L, today, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "Today, I did this test", startDateTime, 8L);

        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
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

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest morning = new TimeEntryRequest("This morning, I did this test", LocalDateTime.of(2020, 1, 1, 9, 0), 4);

        create(2L, morning, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This morning, I did this test", startDateTime, 4L);

        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
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

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2020, 1, 1, 11, 0);
        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test", start, 1);

        create(2L, hour, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This hour, I did this test", start, 1L);
        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
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

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 11, 0);

        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test", start, -1);

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

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest today = new TimeEntryRequest("Today, I did this test", startDateTime, 8);

        // THEN
        postValidation(TimeEntryDef.uriWithArgs(2L), merryToken, today).statusCode(is(FORBIDDEN.getStatusCode()));
    }

    @Test
    void shouldModifyCommentOfTimeEntryDay() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest today = new TimeEntryRequest("Today, I did this test", startDateTime, 8);

        var location = create(2L, today, jimmyToken);

        TimeEntryRequest updatedToday = new TimeEntryRequest("Today, I updated this entry", startDateTime, 8);
        update(updatedToday, location, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "Today, I updated this entry", startDateTime, 8L);

        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldModifyCommentOfTimeEntryHalfDay() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest morning = new TimeEntryRequest("This morning, I did this test", LocalDateTime.of(2020, 1, 1, 9, 0), 4);

        var location = create(2L, morning, jimmyToken);

        TimeEntryRequest updatedMorning = new TimeEntryRequest("This morning, I updated this entry", startDateTime, 4);
        update(updatedMorning, location, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This morning, I updated this entry", startDateTime, 4L);
        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldModifyCommentOfTimeEntryHour() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test", LocalDateTime.of(2020, 1, 1, 9, 0), 1);

        var location = create(2L, hour, jimmyToken);

        TimeEntryRequest updatedHour = new TimeEntryRequest("This hour, I updated this entry", startDateTime, 1);
        update(updatedHour, location, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This hour, I updated this entry", startDateTime, 1L);
        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);
        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldModifyNumberHourOfTimeEntry() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest hour = new TimeEntryRequest("This hour, I did this test", LocalDateTime.of(2020, 1, 1, 9, 0), 1);

        var location = create(2L, hour, jimmyToken);

        TimeEntryRequest updatedHour = new TimeEntryRequest("This hour, I did this test", startDateTime, 2);
        update(updatedHour, location, jimmyToken);

        // THEN
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry = new TimeSheetResponse.TimeEntryResponse(1L, "This hour, I did this test", startDateTime, 2L);
        final var expectedTimeSheetJimmy2 = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry), null, START_DATE);

        var expected = timeKeeperTestUtils.toJson(expectedTimeSheetJimmy2);
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(expected)).statusCode(is(OK.getStatusCode()));
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void should_DeleteTimeEntry() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));

        final Long timeSheetId = 2L;
        final Long timeEntryId1 = 1L;
        final Long timeEntryId2 = 2L;

        // Create two time entries 1 in morning and 1 in afternoon
        LocalDateTime morningStartDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        LocalDateTime afternoonStartDateTime = LocalDateTime.of(2020, 1, 1, 13, 0);
        TimeEntryRequest morning = new TimeEntryRequest("This morning, I did this test", morningStartDateTime, 4);
        TimeEntryRequest afternoon = new TimeEntryRequest("This afternoon, I did this test", afternoonStartDateTime, 4);
        create(timeSheetId, morning, jimmyToken);
        create(timeSheetId, afternoon, jimmyToken);

        // THEN check if time entries has been successfully created
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry1 = new TimeSheetResponse.TimeEntryResponse(timeEntryId1, "This morning, I did this test", morningStartDateTime, 4L);
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry2 = new TimeSheetResponse.TimeEntryResponse(timeEntryId2, "This afternoon, I did this test", afternoonStartDateTime, 4L);

        var expectedTimeSheetJimmy_AfterAddingTimeEntries = timeKeeperTestUtils.toJson(
                new TimeSheetResponse(timeSheetId, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                List.of(expectedTimeEntry1, expectedTimeEntry2), null, START_DATE));

        // Initially two time entries added before deletion so check if they are present.
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(expectedTimeSheetJimmy_AfterAddingTimeEntries)).statusCode(is(OK.getStatusCode()));

        // Now delete afternoon time entry and check only one time entry is present.
        var expectedTimeSheetJimmy_AfterDeletingAfternoonEntry = timeKeeperTestUtils.toJson(
                new TimeSheetResponse(timeSheetId, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                        List.of(expectedTimeEntry1), null, START_DATE));
        deleteValidation(TimeEntryDeleteDef.uriWithMultiId(timeSheetId, timeEntryId2), jimmyToken, Optional.empty()).statusCode(is(OK.getStatusCode()));

        // after successful deletion: TimeSheet should have one time entry
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(expectedTimeSheetJimmy_AfterDeletingAfternoonEntry)).statusCode(is(OK.getStatusCode()));

    }

    @Test
    void should_Throw_NotFoundException_WhenDeletingTimeEntry() {
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final ClientResponse client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final ProjectResponse project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // ID is 2 since Jimmy timesheet is created after Sam's one - Sam is added before Jimmy to the Project
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));

        final Long timeSheetId = 2L;
        final Long timeEntryId1 = 1L;
        final Long timeEntryId2 = 2L;

        // Create time entry for morning
        LocalDateTime morningStartDateTime = LocalDateTime.of(2020, 1, 1, 9, 0);
        TimeEntryRequest morning = new TimeEntryRequest("This morning, I did this test", morningStartDateTime, 4);
        create(timeSheetId, morning, jimmyToken);

        // THEN check if time entries has been successfully created
        TimeSheetResponse.TimeEntryResponse expectedTimeEntry1 = new TimeSheetResponse.TimeEntryResponse(timeEntryId1, "This morning, I did this test", morningStartDateTime, 4L);

        var expectedTimeSheetJimmy_AfterAddingTimeEntries = timeKeeperTestUtils.toJson(
                new TimeSheetResponse(timeSheetId, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(),
                        List.of(expectedTimeEntry1), null, START_DATE));

        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(expectedTimeSheetJimmy_AfterAddingTimeEntries)).statusCode(is(OK.getStatusCode()));

        // Now delete time entry with different ID ,( which is 2L in this case), should throw not found exception
        deleteValidation(TimeEntryDeleteDef.uriWithMultiId(timeSheetId, timeEntryId2), jimmyToken, Optional.empty()).statusCode(is(NOT_FOUND.getStatusCode()));

    }
}