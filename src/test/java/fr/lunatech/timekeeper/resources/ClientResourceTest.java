package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
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
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ClientDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceReader.readValidation;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class ClientResourceTest {

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateClientWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final var client1 = create(new ClientRequest("NewClient", "NewDescription"), token);

        readValidation(String.format("%s/%s", ClientDef.uri, client1.getId()), token)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(client1)));
    }

    @Test
    void shouldNotCreateClientWhenUserProfile() {

        final String token = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post(ClientDef.uri)
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownClient() {

        final String token = getUserAccessToken();

        readValidation(String.format("%s/%s", ClientDef.uri, 4), token)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllClients() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final var client1 = create(new ClientRequest("NewClient", "NewDescription"), adminToken);
        final var client2 = create(new ClientRequest("NewClient2", "NewDescription2"), adminToken);

        readValidation(ClientDef.uri, token)
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(client1, client2)));
    }

    @Test
    void shouldFindAllClientsEmpty() {

        final String token = getUserAccessToken();

        readValidation(ClientDef.uri, token)
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyClientWhenAdminProfile() {

        final String token = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), token);
        update(new ClientRequest("NewClient", "NewDescription2"), String.format("%s/%s", ClientDef.uri, client.getId()), token);

        final var expectedClient = new ClientResponse(client.getId(), "NewClient", "NewDescription2", emptyList());
        readValidation(String.format("%s/%s", ClientDef.uri, client.getId()), token)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

    @Test
    void shouldNotModifyClientWhenUserProfile() {

        final String adminToken = getAdminAccessToken();
        final String token = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), adminToken);

        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .put(String.format("%s/%s", ClientDef.uri, client.getId()))
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}