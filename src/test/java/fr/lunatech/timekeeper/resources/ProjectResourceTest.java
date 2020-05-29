package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TimeSheetDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.putValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.*;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class ProjectResourceTest {


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

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);

        getValidation(ProjectDef.uriWithid(project.getId()), adminToken, OK).body(is(toJson(project)));
    }

    @Test
    void shouldCreateProjectWhenUserProfile() {
        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();
        final var client = create(new ClientRequest("NewClient 2", "Un client créé en tant qu'admin"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "un projet peut aussi etre créé par un user", client.getId(), true, emptyList()), userAccessToken);

        getValidation(ProjectDef.uriWithid(project.getId()), userAccessToken, OK).body(is(toJson(project)));
    }

    @Test
    void shouldNotCreateProjectWithDuplicateName() {
        final String adminToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);

        try {
            create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);
        } catch (HttpTestRuntimeException httpError) {
            assertEquals(400, httpError.getHttpStatus());
            assertEquals("application/json", httpError.getMimeType());
            System.out.println("400");
            assertEquals("application/json", httpError.getMimeType());
        }
    }

    @Test
    void shouldNotFindUnknownProject() {
        final String userAccessToken = getUserAccessToken();
        getValidation(ProjectDef.uriWithid(99999L), userAccessToken, NOT_FOUND);
    }

    @Test
    void shouldFindAllProjectsEmpty() {
        final String userToken = getUserAccessToken();
        getValidation(ProjectDef.uri, userToken, OK).body(is("[]"));
    }

    @Test
    void shouldFindAllPublicProjects() {

        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var client2 = create(new ClientRequest("Client 2", "New Description 2"), adminToken);

        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);
        final var project11 = create(new ProjectRequest("Some Project 11", false, "other description", client1.getId(), true, emptyList()), adminToken);
        final var project20 = create(new ProjectRequest("Some Project 20", true, "some description", client2.getId(), true, emptyList()), adminToken);

        getValidation(ProjectDef.uri, userToken, OK).body(is(listOfTasJson(project10, project11, project20)));
    }

    @Test
    void shouldModifyProjectWithEmptyListOfUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var originalProject = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);
        final var updatedProject = new ProjectRequest("Some Project 10 updated", false, "updated description", client1.getId(), false, emptyList());
        final var expectedUpdatedProject = new ProjectResponse(originalProject.getId()
                , updatedProject.getName()
                , updatedProject.isBillable()
                , updatedProject.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , emptyList()
                , updatedProject.isPublicAccess());

        // WHEN
        update(updatedProject, ProjectDef.uriWithid(originalProject.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriWithid(originalProject.getId()), adminToken, OK).body(is(toJson(expectedUpdatedProject)));
    }

    @Test
    void shouldAddMemberToProject() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);

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
                , newUsers);

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
                , updatedProjectWithTwoUsers.isPublicAccess());

        // WHEN
        update(updatedProjectWithTwoUsers, ProjectDef.uriWithid(project10.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriWithid(project10.getId()), adminToken, OK).body(is(toJson(expectedProject)));
    }

    @Test
    void shouldNotAcceptInvalidListOfUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String merryToken = getUser2AccessToken();

        var sam = create(merryToken);
        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);

        final var userRequest1 = new ProjectRequest.ProjectUserRequest(sam.getId(), true);

        // If we send twice the same user twice it should trigger a SQL Duplicate key exception
        final var newUsers = List.of(userRequest1, userRequest1);

        final var updateProjectRequest = new ProjectRequest("Some Project with the same user"
                , false
                , "updated description"
                , client1.getId()
                , false
                , newUsers);

        // WHEN THEN
        putValidation(ProjectDef.uriWithid(project10.getId()), adminToken, updateProjectRequest, BAD_REQUEST);
    }

    @Test
    void shouldNotAddMemberToProjectWithUnknownUser() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);

        final var userUnknown = new ProjectRequest.ProjectUserRequest(999L, true);

        // If we send twice the same user twice
        final var newUsers = List.of(userUnknown);

        final var updateProjectRequest = new ProjectRequest("Some Project with one unknown user"
                , false
                , "updated description"
                , client1.getId()
                , false
                , newUsers);

        // WHEN THEN
        putValidation(ProjectDef.uriWithid(project10.getId()), adminToken, updateProjectRequest, BAD_REQUEST);
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

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(8L, project, sam, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());
        final var expectedTimeSheetJimmy = new TimeSheetResponse(9L, project, jimmy, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());


        getValidation(TimeSheetDef.uri, adminToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetSam))));
        getValidation(TimeSheetDef.uri, jimmyToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetJimmy))));
    }

    @Test
    void shouldNotCreateTimeSheetForNonProjectMembers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        List<ProjectRequest.ProjectUserRequest> newUsers = Collections.emptyList();

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        // THEN
        getValidation(TimeSheetDef.uri, adminToken, OK).body(is("[]"));
        getValidation(TimeSheetDef.uri, jimmyToken, OK).body(is("[]"));
    }

    @Test
    void shouldCreateTimeSheetForUsersDuringProjectUpdate(){
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);

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
                , newUsers);

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
                , updatedProjectWithTwoUsers.isPublicAccess());

        update(updatedProjectWithTwoUsers, ProjectDef.uriWithid(project.getId()), adminToken);

        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(8L, expectedProject, sam, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());
        final var expectedTimeSheetJimmy = new TimeSheetResponse(9L, expectedProject, jimmy, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());

        getValidation(TimeSheetDef.uri, adminToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetSam))));
        getValidation(TimeSheetDef.uri, jimmyToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetJimmy))));
    }

    @Test
    void shouldNotDeleteTimeSheetWhenMembersAreRemoved(){
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(adminToken);
        var jimmy = create(jimmyToken);
        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        ProjectRequest.ProjectUserRequest samProjectRequest = new ProjectRequest.ProjectUserRequest(sam.getId(), true);
        ProjectRequest.ProjectUserRequest jimmyProjectRequest = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        List<ProjectRequest.ProjectUserRequest> newUsers = List.of(samProjectRequest, jimmyProjectRequest);

        final var project = create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, newUsers), adminToken);

        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project"
                , true
                , "some description"
                , client.getId()
                , true
                , Collections.emptyList());

        final var expectedProject = new ProjectResponse(project.getId()
                , updatedProjectWithTwoUsers.getName()
                , updatedProjectWithTwoUsers.isBillable()
                , updatedProjectWithTwoUsers.getDescription()
                , new ProjectResponse.ProjectClientResponse(client.getId(), client.getName())
                , Collections.emptyList()
                , updatedProjectWithTwoUsers.isPublicAccess());

        update(updatedProjectWithTwoUsers, ProjectDef.uriWithid(project.getId()), adminToken);

        // THEN
        final var expectedTimeSheetSam = new TimeSheetResponse(8L, expectedProject, sam, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());
        final var expectedTimeSheetJimmy = new TimeSheetResponse(9L, expectedProject, jimmy, true, null, null, TimeUnit.HOURLY.toString(), Collections.emptyList());

        getValidation(TimeSheetDef.uri, adminToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetSam))));
        getValidation(TimeSheetDef.uri, jimmyToken, OK).body(is(listOfTasJson(List.of(expectedTimeSheetJimmy))));
    }
}
