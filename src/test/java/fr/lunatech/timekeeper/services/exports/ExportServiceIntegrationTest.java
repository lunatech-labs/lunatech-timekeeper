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

package fr.lunatech.timekeeper.services.exports;

import fr.lunatech.timekeeper.csv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.time.TimeEntry;
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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetPerProjectPerUserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getUserAccessToken;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

@QuarkusTest

@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class ExportServiceIntegrationTest {

    @Inject
    Flyway flyway;

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @Inject
    ExportService exportService;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    final LocalDate START_DATE = LocalDate.now();

    @Test
    void computeImportedTimeEntryTest() {
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
        TimeEntry timeEntry = TimeEntry.findById(1L);

        var result = exportService.computeImportedTimeEntry(timeEntry);
        var expected = new ImportedTimeEntry(
                "Jimmy James",
                "jimmy@lunatech.fr",
                "NewClient",
                "Some Project",
                "",
                "Today, I did this test",
                "Yes",
                "2020-01-01",
                "09:00:00",
                "2020-01-01",
                "17:00:00",
                "08:00:00",
                "",
                ""
                );
        assertEquals(result, expected);
    }

    @Test
    void getTimeEntriesBetweenTwoDateForExportToTogglCsvTest() {
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
        TimeEntryRequest yesterday = new TimeEntryRequest("Yesterday, I did this test", startDateTime.minusDays(1), 8);
        TimeEntryRequest outLastMonth = new TimeEntryRequest("Last Month out, I did this test", startDateTime.minusMonths(1), 8);

        create(2L, outLastMonth, jimmyToken);
        create(2L, yesterday, jimmyToken);
        create(2L, today, jimmyToken);
        TimeEntry timeEntry = TimeEntry.findById(1L);

        try {
            var result = exportService.getTimeEntriesBetweenTwoDateForExportToTogglCsv(startDateTime.toLocalDate().minusDays(1), startDateTime.toLocalDate()).toString();
            var expected = "Email,Duration,Start Time,Start Date,Description,Project,Task,Client,Tags,Billable" +
                    "\r\n" +
                    "jimmy@lunatech.fr,08:00:00,09:00:00,2019-12-31,\"Yesterday, I did this test\",Some Project,,NewClient,,Yes" +
                    "\r\n" +
                    "jimmy@lunatech.fr,08:00:00,09:00:00,2020-01-01,\"Today, I did this test\",Some Project,,NewClient,,Yes" +
                    "\r\n";
            assertEquals(result, expected);
        } catch (IOException ioe){
            assert false;
        }
    }
}