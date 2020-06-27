package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.UserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class UserResourceTest {

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
    void shouldFindUserWhenAdminProfile() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        getValidation(UserDef.uriPlusId(sam.getId()), samToken,OK).body(is(timeKeeperTestUtils.toJson(sam)));
    }

    @Test
    void shouldFindUserWhenUserProfile() {
        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();
        var sam = create(samToken);
        var jimmy = create(jimmyToken);

        getValidation(UserDef.uriPlusId(sam.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(sam)));
        getValidation(UserDef.uriPlusId(jimmy.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(jimmy)));
    }

    @Test
    void shouldFindAllUsers() {
        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(samToken);
        var jimmy = create(jimmyToken);

        getValidation(UserDef.uri, jimmyToken, OK).body(is(timeKeeperTestUtils.listOfTasJson(sam, jimmy)));
    }

    @Test
    void shouldAddJimmyAsMemberOfAProjectAndRetriveThisProjectInUserRequest() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        // Create two Users
        var jimmy = create(jimmyToken);

        // Create a Client
        var client1 = create(new ClientRequest("NewClient", "NewDescription"), samToken);

        // Create a Project
        var project10 = create(new ProjectRequest("Some Project", true, "some description", client1.getId(), true, emptyList()), samToken);

        // Check that jimmy has no project yet
        getValidation(UserDef.uriPlusId(jimmy.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(jimmy)));

        // WHEN
        // Add Jimmy to a project as a simple member
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        final var newUsers = List.of(userRequest2);
        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project with jimmy that is not a manager"
                , false
                , "updated description"
                , client1.getId()
                , true
                , newUsers);

        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project10.getId()), samToken);

        final List<UserResponse.ProjectUserResponse> expectedProjectUsers = List.of(
                new UserResponse.ProjectUserResponse(project10.getId()
                        , false
                        , updatedProjectWithTwoUsers.getName()
                        , updatedProjectWithTwoUsers.isPublicAccess()
                )
        );

        UserResponse expectedResult = new UserResponse(
                jimmy.getId(),
                jimmy.getName(),
                jimmy.getEmail(),
                jimmy.getPicture(),
                jimmy.getProfiles(),
                expectedProjectUsers
        );


        // THEN
        getValidation(UserDef.uriPlusId(jimmy.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(expectedResult)));
    }

    @Test
    void shouldAddSamAsMemberOfAProjectAndRetriveThisProjectInUserRequest() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        // Create two Users
        var jimmy = create(jimmyToken);
        var teamLeadUser = create(samToken);

        // Create a Client
        var client1 = create(new ClientRequest("NewClient", "NewDescription"), samToken);

        // Create a Project
        var project10 = create(new ProjectRequest("Some Project", true, "some description", client1.getId(), true, emptyList()), samToken);

        // Check that teamLeadUser has no project yet
        getValidation(UserDef.uriPlusId(teamLeadUser.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(teamLeadUser)));

        // WHEN
        // Add teamLeadUser to a project as a team lead member
        final var userRequest1 = new ProjectRequest.ProjectUserRequest(teamLeadUser.getId(), true);
        // Add jimmy as member
        final var userRequest2 = new ProjectRequest.ProjectUserRequest(jimmy.getId(), false);

        final var newUsers = List.of(userRequest1, userRequest2);
        final var updatedProjectWithTwoUsers = new ProjectRequest("Some Project with SAM as a TeamLead and Jimmy as member"
                , true
                , "updated description"
                , client1.getId()
                , false
                , newUsers);

        update(updatedProjectWithTwoUsers, ProjectDef.uriPlusId(project10.getId()), samToken);

        final List<UserResponse.ProjectUserResponse> expectedProjectUsers = List.of(
                new UserResponse.ProjectUserResponse(project10.getId()
                        , true // <--- Sam is a manager
                        , updatedProjectWithTwoUsers.getName()
                        , updatedProjectWithTwoUsers.isPublicAccess()
                )
        );

        UserResponse expectedResult = new UserResponse(
                teamLeadUser.getId(),
                teamLeadUser.getName(),
                teamLeadUser.getEmail(),
                teamLeadUser.getPicture(),
                teamLeadUser.getProfiles(),
                expectedProjectUsers
        );


        final List<UserResponse.ProjectUserResponse> expectedProjectUsers2 = List.of(
                new UserResponse.ProjectUserResponse(project10.getId()
                        , false // <---- jimmy is not a manager
                        , updatedProjectWithTwoUsers.getName()
                        , updatedProjectWithTwoUsers.isPublicAccess()
                )
        );
        UserResponse expectedResult2 = new UserResponse(
                jimmy.getId(),
                jimmy.getName(),
                jimmy.getEmail(),
                jimmy.getPicture(),
                jimmy.getProfiles(),
                expectedProjectUsers2
        );

        // THEN
        getValidation(UserDef.uriPlusId(teamLeadUser.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(expectedResult)));
        getValidation(UserDef.uriPlusId(jimmy.getId()), jimmyToken, OK).body(is(timeKeeperTestUtils.toJson(expectedResult2)));
    }
}