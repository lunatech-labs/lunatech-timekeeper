package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
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

        final String samToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), samToken);

        readValidation(client.getId(), ClientDef.uri, samToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(client)));
    }

    @Test
    void shouldNotCreateClientWhenUserProfile() {

        final String jimmyToken = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");

        given()
                .auth().preemptive().oauth2(jimmyToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client)
                .post(ClientDef.uri)
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldNotFindUnknownClient() {

        final String jimmyToken = getUserAccessToken();

        final  Long NO_EXISTING_CLIENT_ID = 243L;

        readValidation(NO_EXISTING_CLIENT_ID, ClientDef.uri, jimmyToken)
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFindAllClients() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("NewClient", "NewDescription"), samToken);
        final var client2 = create(new ClientRequest("NewClient2", "NewDescription2"), samToken);

        readValidation(ClientDef.uri, jimmyToken)
                .statusCode(OK.getStatusCode())
                .body(is(TestUtils.listOfTasJson(client1, client2)));
    }

    @Test
    void shouldFindAllClientsEmpty() {

        final String jimmyToken = getUserAccessToken();

        readValidation(ClientDef.uri, jimmyToken)
                .statusCode(OK.getStatusCode())
                .body(is("[]"));
    }

    @Test
    void shouldModifyClientWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), samToken);
        update(new ClientRequest("NewClient", "NewDescription2"), String.format("%s/%s", ClientDef.uri, client.getId()), samToken);

        final var expectedClient = new ClientResponse(client.getId(), "NewClient", "NewDescription2", emptyList());
        readValidation(client.getId(), ClientDef.uri, samToken)
                .statusCode(OK.getStatusCode())
                .body(is(toJson(expectedClient)));
    }

    @Test
    void shouldNotModifyClientWhenUserProfile() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), samToken);

        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");
        given()
                .auth().preemptive().oauth2(jimmyToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(client2)
                .put(String.format("%s/%s", ClientDef.uri, client.getId()))
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}