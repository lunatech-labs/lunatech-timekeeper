package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceReader.readValidation;
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

        readValidation(project.getId(), ProjectDef.uri, adminToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(project)));
    }

    @Test
    void shouldCreateProjectWhenUserProfile() {

        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();
        final var client = create(new ClientRequest("NewClient 2", "Un client créé en tant qu'admin"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "un projet peut aussi etre créé par un user", client.getId(), true, emptyList()), userAccessToken);

        readValidation(project.getId(), ProjectDef.uri, userAccessToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(project)));
    }

    @Test
    void shouldNotCreateProjectWithDuplicateName() {

        final String adminToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);

        try {
            create(new ProjectRequest("Some Project", true, "some description", client.getId(), true, emptyList()), adminToken);
        }catch (HttpTestRuntimeException httpError){
            assertEquals(409, httpError.getHttpStatus());
            assertEquals("application/json", httpError.getMimeType());
        }
    }


    @Test
    void shouldNotFindUnknownProject() {
        final String userAccessToken = getUserAccessToken();

        readValidation(9999L, ProjectDef.uri, userAccessToken)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllProjectsEmpty() {
        final String userToken = getUserAccessToken();

        readValidation(ProjectDef.uri, userToken)
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldFindAllPublicProjects() {

        final String adminToken = getAdminAccessToken();
        final String userToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("Client 1", "New Description 1"), adminToken);
        final var client2 = create(new ClientRequest("Client 2", "New Description 2"), adminToken);

        final var projec10 = create(new ProjectRequest("Some Project 10", true, "some description", client1.getId(), true, emptyList()), adminToken);
        final var projec11 = create(new ProjectRequest("Some Project 11", false, "other description", client1.getId(), true, emptyList()), adminToken);
        final var projec20 = create(new ProjectRequest("Some Project 20", true, "some description", client2.getId(), true, emptyList()), adminToken);

        readValidation(ProjectDef.uri, userToken)
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(projec10, projec11,projec20)));
    }

    @Test
    void shouldModifyProject() {

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