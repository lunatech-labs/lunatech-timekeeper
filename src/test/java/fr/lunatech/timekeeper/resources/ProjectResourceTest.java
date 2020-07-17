package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetPerProjectPerUserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.putValidation;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class ProjectResourceTest {

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
    void shouldCreateProjectWhenAdminProfile() {
        final String adminToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient1", "NewDescription"), adminToken);
        final var project = create(new ProjectRequest("Some Project1", true, "some description", client.getId(), true, emptyList(), 1L), adminToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(project))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldCreateProjectWhenUserProfile() {
        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();
        final var client = create(new ClientRequest("NewClient 2", "Un client créé en tant qu'admin"), adminToken);

        final var project = create(new ProjectRequest("Some Project 2", true, "un projet peut aussi etre créé par un user", client.getId(), true, emptyList(), 1L), userAccessToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), userAccessToken).body(is(timeKeeperTestUtils.toJson(project))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotCreateProjectWithDuplicateName() {
        final String adminToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient 3", "NewDescription"), adminToken);
        create(new ProjectRequest("Same name", true, "some description", client.getId(), true, emptyList(), 1L), adminToken);
        ProjectRequest projectRequestDuplicatedName = new ProjectRequest("Same name", false, "some other description", client.getId(), true, emptyList(), 1L);
        try {
            create(projectRequestDuplicatedName, adminToken);
        } catch (HttpTestRuntimeException httpError) {
            assertEquals("application/json", httpError.getMimeType());
            assertEquals(400, httpError.getHttpStatus());
        }
    }

    @Test
    void shouldNotFindUnknownProject() {
        final String userAccessToken = getUserAccessToken();
        getValidation(ProjectDef.uriPlusId(99999L), userAccessToken).statusCode(is(NOT_FOUND.getStatusCode()));
    }

    @Test
    void shouldFindAllProjectsEmpty() {
        final String userToken = getUserAccessToken();
        getValidation(ProjectDef.uri, userToken).body(is("[]")).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldFindAllPublicProjects() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("Client 10", "New Description 1"), adminToken);
        final var client2 = create(new ClientRequest("Client 20", "New Description 2"), adminToken);

        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var project11 = create(new ProjectRequest("Some Project 11", false, "other description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var project20 = create(new ProjectRequest("Some Project 20", true, "some description", client2.getId(), true, emptyList(), 1L), adminToken);

        getValidation(ProjectDef.uri, userToken).body(is(timeKeeperTestUtils.listOfTasJson(project10, project11, project20))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldModifyProjectWithEmptyListOfUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);
        final var originalProject = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var updatedProject = new ProjectRequest("Some Project 10 updated", false, "updated description", client1.getId(), false, emptyList(), 1L);
        final var expectedUpdatedProject = new ProjectResponse(originalProject.getId()
                , updatedProject.getName()
                , updatedProject.isBillable()
                , updatedProject.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , emptyList()
                , updatedProject.isPublicAccess()
                , 2L
        );

        // WHEN
        update(updatedProject, ProjectDef.uriPlusId(originalProject.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriPlusId(originalProject.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedUpdatedProject))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotModifyProjectAsUserNotInProject() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);
        final var originalProject = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var updatedProject = new ProjectRequest("Some Project 10 updated", false, "updated description", client1.getId(), false, emptyList(), 1L);
        // WHEN
        putValidation(ProjectDef.uriPlusId(originalProject.getId()), userToken, updatedProject).statusCode(is(FORBIDDEN.getStatusCode()));
    }

    @Test
    void shouldNotModifyProjectAsUserAsMember() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);

        var sam = create(adminToken);
        var jimmy = create(userToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        final var expectedProjectUsers = List.of(userRequest1, userRequest2);

        final var projectRequest = new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, expectedProjectUsers, 1L);
        final var projectCreated = create(projectRequest, adminToken);

        // WHEN
        putValidation(ProjectDef.uriPlusId(projectCreated.getId()), userToken, projectRequest).statusCode(is(FORBIDDEN.getStatusCode()));
    }

    @Test
    void shouldModifyProjectAsUserAsTeamLeader() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);

        var sam = create(adminToken);
        var jimmy = create(userToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), true);

        final var projectUserRequests = List.of(userRequest1, userRequest2);

        final var projectRequest = new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, projectUserRequests, 1L);
        final var projectCreated = create(projectRequest, adminToken);

        final var updatedProject = new ProjectRequest("Some Project Updated", true, "some description", client1.getId(), true, projectUserRequests, 1L);

        final var expectedProjectUsers = List.of(
                new ProjectResponse.ProjectUserResponse(sam.getId(), true, sam.getName(), sam.getPicture()),
                new ProjectResponse.ProjectUserResponse(jimmy.getId(), true, jimmy.getName(), jimmy.getPicture())
        );

        final var expectedUpdatedProject = new ProjectResponse(projectCreated.getId()
                , updatedProject.getName()
                , updatedProject.isBillable()
                , updatedProject.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , expectedProjectUsers
                , updatedProject.isPublicAccess()
                , 2L
        );
        // WHEN
        update(updatedProject, ProjectDef.uriPlusId(projectCreated.getId()), userToken).statusCode(is(NO_CONTENT.getStatusCode()));
        getValidation(ProjectDef.uriPlusId(projectCreated.getId()), userToken).body(is(timeKeeperTestUtils.toJson(expectedUpdatedProject))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldAccessPrivateProjectAsTeamLeader() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);

        var sam = create(adminToken);
        var jimmy = create(userToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), true);

        final var newProjectUsers = List.of(userRequest1, userRequest2);

        final var projectRequest = new ProjectRequest("Some Project 10", true, "some description", client1.getId(), false, newProjectUsers, 1L);
        final var projectCreated = create(projectRequest, adminToken);
        getValidation(ProjectDef.uriPlusId(projectCreated.getId()), userToken).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldAccessPrivateProjectAsSimpleMember() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);

        var sam = create(adminToken);
        var jimmy = create(userToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        final var newProjectUsers = List.of(userRequest1, userRequest2);

        final var projectRequest = new ProjectRequest("Some Project 10", true, "some description", client1.getId(), false, newProjectUsers, 1L);
        final var projectCreated = create(projectRequest, adminToken);
        getValidation(ProjectDef.uriPlusId(projectCreated.getId()), userToken).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldAccessToPublicProjects() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);

        final var projectRequest = new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, Collections.emptyList(), 1L);
        final var projectCreated = create(projectRequest, adminToken);
        getValidation(ProjectDef.uriPlusId(projectCreated.getId()), userToken).statusCode(is(OK.getStatusCode()));
        getValidation(ProjectDef.uriPlusId(projectCreated.getId()), adminToken).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldJoinAProjectAsAdmin() {
        //Create the project
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        final var sam = create(adminToken);
        final var projectRequest = new ProjectRequest("Some Project", true, "some description", null, true, Collections.emptyList(), 1L);
        final var projectCreated = create(projectRequest, adminToken);
        update(ProjectDef.uriPlusId(projectCreated.getId())+"/join", userToken).statusCode(is(NO_CONTENT.getStatusCode()));
    }

    @Test
    void shouldJoinAProjectAsUser() {
        //Create the project
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        create(userToken);
        final var projectRequest = new ProjectRequest("Some Project", true, "some description", null, true, Collections.emptyList(), 1L);
        final var projectCreated = create(projectRequest, adminToken);
        update(ProjectDef.uriPlusId(projectCreated.getId())+"/join", userToken).statusCode(is(NO_CONTENT.getStatusCode()));
    }

    @Test
    void shouldAddMemberToProject() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("Client 146", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 147", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);

        var sam = create(samToken);
        var jimmy = create(jimmyToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        final var newUsers = List.of(userRequest1, userRequest2);
        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project with 2 users"
                , false
                , "updated description"
                , client1.getId()
                , false
                , newUsers
                , 1L);

        final var expectedProjectUsers = List.of(
                new ProjectResponse.ProjectUserResponse(sam.getId(), true, sam.getName(), sam.getPicture())
                , new ProjectResponse.ProjectUserResponse(jimmy.getId(), false, jimmy.getName(), jimmy.getPicture())
        );
        final var expectedProject = new ProjectResponse(project10.getId()
                , updatedProjectWithTwoUsers.getName()
                , updatedProjectWithTwoUsers.isBillable()
                , updatedProjectWithTwoUsers.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , expectedProjectUsers
                , updatedProjectWithTwoUsers.isPublicAccess()
                , 2L);

        // WHEN
        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project10.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriPlusId(project10.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedProject))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotAcceptInvalidListOfUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String merryToken = getUser2AccessToken();

        var sam = create(merryToken);
        final var client1 = create(new ClientRequest("Client", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project with the same user", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);

        // If we send twice the same user twice it should trigger a SQL Duplicate key exception
        final var newUsers = List.of(userRequest1, userRequest1);

        final var updateProjectRequest = new ProjectRequest("Some Project with the same user"
                , false
                , "updated description"
                , client1.getId()
                , false
                , newUsers
                , 1L
        );

        // WHEN THEN
        putValidation(ProjectDef.uriPlusId(project10.getId()), adminToken, updateProjectRequest).statusCode(is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    void shouldNotAddMemberToProjectWithUnknownUser() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);

        final var userUnknown = new ProjectRequest.ProjectUserRequest(999L, true);

        // If we send twice the same user twice
        final var newUsers = List.of(userUnknown);

        final var updateProjectRequest = new ProjectRequest("Some Project with one unknown user"
                , false
                , "updated description"
                , client1.getId()
                , false
                , newUsers
                , 1L);

        // WHEN THEN
        putValidation(ProjectDef.uriPlusId(project10.getId()), adminToken, updateProjectRequest).statusCode(is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    void shouldCreateTimeSheetForProjectMembers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(1L, project, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), jimmyToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), sam.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotCreateTimeSheetForNonProjectMembers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        List<ProjectRequest.ProjectUserRequest> newUsers = Collections.emptyList();

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        // THEN return 404 not found for the timesheets
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), jimmyToken).statusCode(is(NOT_FOUND.getStatusCode()));
    }

    @Test
    void shouldRetrieveTimeSheetForProjectMembers() {
        // GIVEN : 2 users of 1 project for 1 client
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        // WHEN creating the project, timesheets for all members are generated
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, project, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        // THEN : I can see the timeSheet by project by member
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
    }


    @Test
    void shouldCreateTimeSheetForUsersDuringProjectUpdate() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList(), 1L), adminToken);

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        final var jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project"
                , true
                , "some description"
                , client.getId()
                , true
                , newUsers
                , 1L);

        final var expectedProjectUsers = List.of(
                new ProjectResponse.ProjectUserResponse(sam.getId(), true, sam.getName(), sam.getPicture())
                , new ProjectResponse.ProjectUserResponse(jimmy.getId(), false, jimmy.getName(), jimmy.getPicture())
        );
        final var expectedProject = new ProjectResponse(project.getId()
                , updatedProjectWithTwoUsers.getName()
                , updatedProjectWithTwoUsers.isBillable()
                , updatedProjectWithTwoUsers.getDescription()
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , expectedProjectUsers
                , updatedProjectWithTwoUsers.isPublicAccess()
                , 2L);

        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project.getId()), adminToken);

        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(1L, expectedProject, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, expectedProject, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), jimmyToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), sam.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotDeleteTimeSheetWhenMembersAreRemoved() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project"
                , true
                , "some description"
                , client.getId()
                , true
                , Collections.emptyList()
                , 1L);

        final var expectedProject = new ProjectResponse(project.getId()
                , updatedProjectWithTwoUsers.getName()
                , updatedProjectWithTwoUsers.isBillable()
                , updatedProjectWithTwoUsers.getDescription()
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , Collections.emptyList()
                , updatedProjectWithTwoUsers.isPublicAccess()
                , 2L
        );

        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project.getId()), adminToken);
        getValidation(ProjectDef.uriPlusId(project.getId()), adminToken)
                .body(is(timeKeeperTestUtils.toJson(expectedProject)))
                .statusCode(is(OK.getStatusCode()));
        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(1L, expectedProject, sam.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);
        final var expectedTimeSheetJimmy = new TimeSheetResponse(2L, expectedProject, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), jimmyToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), sam.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetSam))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldNotChangeListOfTimeSheetsForExistingMembersWhenANewMemberIsAdded() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(jimmyProjectRequest);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);

        List<ProjectRequest.ProjectUserRequest> newUsers2 = List.of(jimmyProjectRequest, samProjectRequest);

        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project"
                , true
                , "some description"
                , client.getId()
                , true
                , newUsers2
                , 1L);

        final var expectedProjectUsers = List.of(
                new ProjectResponse.ProjectUserResponse(jimmy.getId(), false, jimmy.getName(), jimmy.getPicture())
                , new ProjectResponse.ProjectUserResponse(sam.getId(), true, sam.getName(), sam.getPicture())
        );

        final var expectedProject = new ProjectResponse(project.getId()
                , updatedProjectWithTwoUsers.getName()
                , updatedProjectWithTwoUsers.isBillable()
                , updatedProjectWithTwoUsers.getDescription()
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , expectedProjectUsers
                , updatedProjectWithTwoUsers.isPublicAccess()
                , 2L
        );

        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project.getId()), adminToken);

        // THEN
        final var expectedTimeSheetJimmy = new TimeSheetResponse(1L, expectedProject, jimmy.getId(), TimeUnit.HOURLY, true, null, null, TimeUnit.DAY.toString(), Collections.emptyList(),null);

        getValidation(TimeSheetPerProjectPerUserDef.uriWithMultiId(project.getId(), jimmy.getId()), jimmyToken).body(is(timeKeeperTestUtils.toJson(expectedTimeSheetJimmy))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldLoadAProjectWithoutUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        List<ProjectRequest.ProjectUserRequest> newUsers = Collections.emptyList();
        Map<String, String> params = new HashMap<>();
        params.put("optimized", "true");
        final var fullProject = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers, 1L), adminToken);
        final var attemptProjectResponse = new ProjectResponse(
                1L
                , "Some Project"
                , true
                , "some description"
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , null
                , true
                , 1L
        );
        // THEN
        getValidation(ProjectDef.uriPlusId(fullProject.getId(), params), adminToken).body(is(timeKeeperTestUtils.toJson(attemptProjectResponse))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldIncrementTheProjectVersionNumbeWhenUpdated() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);
        final var originalProject = create(new ProjectRequest("Some Project for concurrent access", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var newProjectVersion01 = new ProjectRequest("this is version 1"
                , false, "updated description"
                , client1.getId()
                , false
                , emptyList()
                , 1L);

        final var updatedVersion02 = new ProjectRequest("this is version 2"
                , true
                , "updated description"
                , client1.getId()
                , false
                , emptyList()
                , 2L); // It means that you edit version 2 and you want to create a version 3

        final var expectedUpdatedProject = new ProjectResponse(originalProject.getId()
                , updatedVersion02.getName()
                , updatedVersion02.isBillable()
                , updatedVersion02.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , emptyList()
                , updatedVersion02.isPublicAccess()
                , 3L);

        // WHEN
        update(newProjectVersion01, ProjectDef.uriPlusId(originalProject.getId()), adminToken);
        update(updatedVersion02, ProjectDef.uriPlusId(originalProject.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriPlusId(originalProject.getId()), adminToken).body(is(timeKeeperTestUtils.toJson(expectedUpdatedProject))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldRejectAnUpdateOnAProjectIfItWasAlreadyUpdated() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 11", "New Description 1"), adminToken);
        final var originalProject = create(new ProjectRequest("Some Project for concurrent access", true, "some description", client1.getId(), true, emptyList(), 1L), adminToken);
        final var newProjectVersion01 = new ProjectRequest("this is version 1"
                , false, "updated description"
                , client1.getId()
                , false
                , emptyList()
                , 1L);

        final var updatedVersion02 = new ProjectRequest("this is version 1 bis that should not be created"
                , true
                , "updated description"
                , client1.getId()
                , false
                , emptyList()
                , 1L);


        update(newProjectVersion01, ProjectDef.uriPlusId(originalProject.getId()), adminToken);

        // WHEN
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(updatedVersion02)
                .put(ProjectDef.uriPlusId(originalProject.getId()))
                .then()
                .statusCode(CONFLICT.getStatusCode());

    }

    @Test
    void shouldFindAllPrivateProjectsIfMember() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        var jimmy = create(userToken);

        final var client1 = create(new ClientRequest("Client 10", "This is a client for test"), adminToken);

        final var project10 = create(new ProjectRequest("Some private project with a user", true, "user is member of this private project", client1.getId(), false, emptyList(), 1L), adminToken);
        create(new ProjectRequest("Some private project without the user", false, "user is not member of this project", client1.getId(), false, emptyList(), 1L), adminToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);
        final var newUsers = List.of(userRequest1);

        final var updatedProjectWithOneUser = new ProjectRequest("Some private project with a user"
                , false
                , "user is now a member if this project"
                , client1.getId()
                , false
                , newUsers
                , 1L);

        final var expectedUpdatedProject = new ProjectResponse(project10.getId()
                , updatedProjectWithOneUser.getName()
                , updatedProjectWithOneUser.isBillable()
                , updatedProjectWithOneUser.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , List.of(new ProjectResponse.ProjectUserResponse(jimmy.getId(), false, jimmy.getName(), jimmy.getPicture()))
                , updatedProjectWithOneUser.isPublicAccess()
                , 2L);

        update(updatedProjectWithOneUser, ProjectDef.uriPlusId(project10.getId()), adminToken);

        getValidation(ProjectDef.uri, userToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedUpdatedProject))).statusCode(is(OK.getStatusCode()));
    }

    @Test
    void shouldFindAllPrivateProjectsIfTeamLeader() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();
        var jimmy = create(userToken);

        final var client1 = create(new ClientRequest("Client 10", "This is a client for test"), adminToken);

        final var project10 = create(new ProjectRequest("Some private project with a user", true, "user is member of this private project", client1.getId(), false, emptyList(), 1L), adminToken);
        create(new ProjectRequest("Some private project without the user", false, "user is not member of this project", client1.getId(), false, emptyList(), 1L), adminToken);

        var isTeamLeader = true;

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), isTeamLeader);
        final var newUsers = List.of(userRequest1);

        final var updatedProjectWithOneUser = new ProjectRequest("Some private project with a user"
                , false
                , "user is now a member if this project"
                , client1.getId()
                , false
                , newUsers
                , 1L);

        final var expectedUpdatedProject = new ProjectResponse(project10.getId()
                , updatedProjectWithOneUser.getName()
                , updatedProjectWithOneUser.isBillable()
                , updatedProjectWithOneUser.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , List.of(new ProjectResponse.ProjectUserResponse(jimmy.getId(), isTeamLeader, jimmy.getName(), jimmy.getPicture()))
                , updatedProjectWithOneUser.isPublicAccess()
                , 2L);

        update(updatedProjectWithOneUser, ProjectDef.uriPlusId(project10.getId()), adminToken);

        getValidation(ProjectDef.uri, userToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedUpdatedProject))).statusCode(is(OK.getStatusCode()));
    }
}
