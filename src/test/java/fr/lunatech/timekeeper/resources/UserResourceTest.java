package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.ResourceReader;
import fr.lunatech.timekeeper.resources.utils.TestUtils;
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
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.UserDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class UserResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldFindUserWhenAdminProfile() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        getValidation(UserDef.uriWithid(2L), samToken,OK).body(is(TestUtils.toJson(sam)));
    }

    @Test
    void shouldFindAllUsers() {
        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        var sam = create(samToken);
        var jimmy = create(jimmyToken);

        getValidation(UserDef.uri, jimmyToken, OK).body(is(TestUtils.listOfTasJson(sam, jimmy)));
    }

    //TODO
    /*
    @Test
    void shouldFindUserMemberOfProject() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final OrganizationRequest organization = new OrganizationRequest("NewClient", "lunatech.fr");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(organization)
                .post("/api/organizations")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/organizations/1"));

        final UserRequest user1 = new UserRequest("Sam", "Huel", "sam@gmail.com", "sam.png", List.of(Admin));
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user1)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/2"));

        final UserRequest user2 = new UserRequest("Jimmy", "Pastore", "jimmy@gmail.com", "jimmy.png", List.of(User));
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(user2)
                .post("/api/users")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/users/3"));

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post("/api/clients")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/clients/4"));



        final ProjectRequest project = new ProjectRequest("Pepito", true, "New project", 4L,1L, false);
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(project)
                .post("/api/projects")
                .then()
                .statusCode(CREATED.getStatusCode())
                .header(LOCATION, endsWith("/api/projects/5"));

        final MemberRequest member1 = new MemberRequest(2L, Role.TeamLeader);
        final MemberRequest member2 = new MemberRequest(3L, Role.Developer);
        final MembersUpdateRequest members = new MembersUpdateRequest(List.of(member1, member2));
        given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(members)
                .put("/api/projects/5/members")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());


        final MemberResponse expectedMemberResponse1 = new MemberResponse(6L, 2L, Role.TeamLeader, 5L);
        final MemberResponse expectedMemberResponse2 = new MemberResponse(7L, 3L, Role.Developer, 5L);
        final UserResponse expectedUserResponse1 = new UserResponse(2L, "Sam", "Huel", "sam@gmail.com", "sam.png", List.of(Admin), List.of(expectedMemberResponse1),1L);
        final UserResponse expectedUserResponse2 = new UserResponse(3L, "Jimmy", "Pastore", "jimmy@gmail.com", "jimmy.png", List.of(User), List.of(expectedMemberResponse2),1L);
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get("/api/users")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(expectedUserResponse1, expectedUserResponse2)));
    }*/
}