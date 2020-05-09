package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.ProjectUser;
import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ClientDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
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

        getValidation(ProjectDef.uri, userToken, OK).body(is(TestUtils.listOfTasJson(project10, project11, project20)));
    }

    @Test
    void shouldModifyProjectWithEmptyListOfUsers() {
        // GIVEN
        final String adminToken = getAdminAccessToken();
        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var project10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);
        final var updatedProject = new ProjectRequest("Some Project 10 updated",false, "updated description", client1.getId(),false, emptyList());
        final var expectedProject = new ProjectResponse(project10.getId()
                , updatedProject.getName()
                , updatedProject.isBillable()
                , updatedProject.getDescription()
                , new ProjectResponse.ProjectClientResponse(client1.getId(), client1.getName())
                , emptyList()
                , updatedProject.isPublicAccess());

        // WHEN
        update(updatedProject, ProjectDef.uriWithid(project10.getId()), adminToken);

        // THEN
        getValidation(ProjectDef.uriWithid(project10.getId()), adminToken, OK).body(is(toJson(expectedProject)));
    }

    @Test
    void shouldAddMemberToProject() {

    }

    @Test
    void shouldNotAddMemberTwiceToProject() {

    }

    @Test
    void shouldNotAddMemberToProjectWithUnknownUser() {

    }

    @Test
    void shouldModifyMembersAndGetProjectMembers() {

    }
}