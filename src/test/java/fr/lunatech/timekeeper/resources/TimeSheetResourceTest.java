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

import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.putValidation;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getUserAccessToken;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@QuarkusTest

@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
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

    final private LocalDate START_DATE = LocalDate.now();

    @Test
    void shouldFindTimeSheetById() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        final var sam = create(adminToken);
        final var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);


        final var expectedTimeSheetSam = new TimeSheetResponse(1L, project, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);

        // THEN
        getValidation(TimeSheetDef.uriPlusId(1L), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));
        getValidation(TimeSheetDef.uriPlusId(2L), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldUpdateTimeSheetById() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        Long timeSheetId = 1L;

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);
        // verify first version
        final var expectedTimeSheetSam = new TimeSheetResponse(timeSheetId, project, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(), null, START_DATE);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));

        // WHEN : AND the timesheet is updated (adding a end date a maxDuration and changing units)
        LocalDate newEndDate = LocalDate.now().plusMonths(2L);
        TimeSheetRequest updatedTimeSheet = new TimeSheetRequest(
                TimeUnit.DAY,
                true,
                newEndDate,
                60,
                TimeUnit.DAY,
                START_DATE
        );
        putValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken, timeKeeperTestUtils.toJson(updatedTimeSheet)).statusCode(NO_CONTENT.getStatusCode());

        // THEN get the updated version
        final var expectedUpdatedTimeSheetSam = new TimeSheetResponse(timeSheetId, project, sam.getId(), TimeUnit.DAY, true, newEndDate, 60, TimeUnit.DAY.toString(), Collections.emptyList(), 60L, START_DATE);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).body(is(timeKeeperTestUtils.toJson(expectedUpdatedTimeSheetSam))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldHaveDefaultStartDateDuringCreation() {
        // GIVEN : a project with 1 member
        final String adminToken = getAdminAccessToken();
        final UserResponse sam = create(adminToken);
        final ClientResponse clientResponse = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        Long timeSheetId = 1L;

        // WHEN : the project is created, a time sheet will have default start which should date of creation
        create(new ProjectRequest("Some Project", true, "some description", clientResponse.getId(), true, newUsers, 1L), adminToken);

        assertThat(getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken).extract().body().as(TimeSheetResponse.class).startDate, is(START_DATE));

    }

    @Test
    void shouldUpdateTimeSheetWithNoEndDateTime() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        Long timeSheetId = 1L;

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);
        // verify first version
        final var expectedTimeSheetSam = new TimeSheetResponse(timeSheetId,
                project,
                sam.getId(),
                TimeUnit.HOURLY,
                true,
                null,
                null,
                TimeUnit.DAY.toString(),
                Collections.emptyList(),
                null,
                START_DATE);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken)
                .body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam)))
                .statusCode(CoreMatchers.is(OK.getStatusCode()));

        // WHEN : AND the timesheet is updated (adding a end date a maxDuration and changing units)
        TimeSheetRequest updatedTimeSheet = new TimeSheetRequest(
                TimeUnit.DAY,
                true,
                null,
                60,
                TimeUnit.DAY,
                START_DATE
        );
        putValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken, timeKeeperTestUtils.toJson(updatedTimeSheet)).statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotUpdateTimeSheetWithStartDateBeforeEndDate() {
        // GIVEN : a project with 2 member
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest);
        Long timeSheetId = 1L;

        // WHEN : the project is created, a time sheet is generated for all user
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);
        // verify first version
        final var expectedTimeSheetSam = new TimeSheetResponse(timeSheetId,
                project,
                sam.getId(),
                TimeUnit.HOURLY,
                true,
                null,
                null,
                TimeUnit.DAY.toString(),
                Collections.emptyList(),
                null,
                START_DATE);
        getValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken)
                .body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam)))
                .statusCode(CoreMatchers.is(OK.getStatusCode()));

        // WHEN : AND the timesheet is updated (adding a end date a maxDuration and changing units)
        LocalDate newEndDate = LocalDate.now().minusMonths(2L);
        TimeSheetRequest updatedTimeSheet = new TimeSheetRequest(
                TimeUnit.DAY,
                true,
                newEndDate,
                60,
                TimeUnit.DAY,
                START_DATE
        );
        putValidation(TimeSheetDef.uriPlusId(timeSheetId), adminToken, timeKeeperTestUtils.toJson(updatedTimeSheet)).statusCode(500);
    }
}