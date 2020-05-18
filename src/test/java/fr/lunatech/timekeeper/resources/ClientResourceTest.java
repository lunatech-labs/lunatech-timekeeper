package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.TestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ClientDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.*;
import static fr.lunatech.timekeeper.resources.utils.TestUtils.toJson;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
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
        getValidation(ClientDef.uriWithid(client.getId()), samToken, OK).body(is(toJson(client)));
    }

    @Test
    void shouldNotCreateClientWhenUserProfile() {
        final String jimmyToken = getUserAccessToken();

        final ClientRequest client = new ClientRequest("NewClient", "NewDescription");
        postValidation(ClientDef.uri, jimmyToken, client, FORBIDDEN);
    }

    @Test
    void shouldNotFindUnknownClient() {

        final String jimmyToken = getUserAccessToken();

        final Long NO_EXISTING_CLIENT_ID = 243L;

        getValidation(ClientDef.uriWithid(NO_EXISTING_CLIENT_ID), jimmyToken, NOT_FOUND);

    }

    @Test
    void shouldFindAllClients() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client1 = create(new ClientRequest("NewClient", "NewDescription"), samToken);
        final var client2 = create(new ClientRequest("NewClient2", "NewDescription2"), samToken);

        getValidation(ClientDef.uri, jimmyToken, OK).body(is(TestUtils.listOfTasJson(client1, client2)));
    }

    @Test
    void shouldFindAllClientsEmpty() {

        final String jimmyToken = getUserAccessToken();

        getValidation(ClientDef.uri, jimmyToken, OK).body(is("[]"));
    }

    @Test
    void shouldModifyClientWhenAdminProfile() {

        final String samToken = getAdminAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), samToken);
        update(new ClientRequest("NewClient", "NewDescription2"), ClientDef.uriWithid(client.getId()), samToken);

        final var expectedClient = new ClientResponse(client.getId(), "NewClient", "NewDescription2", emptyList());

        getValidation(ClientDef.uriWithid(client.getId()), samToken, OK).body(is(toJson(expectedClient)));
    }

    @Test
    void shouldNotModifyClientWhenUserProfile() {

        final String samToken = getAdminAccessToken();
        final String jimmyToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient", "NewDescription"), samToken);

        final ClientRequest client2 = new ClientRequest("NewClient", "NewDescription2");

        putValidation(ClientDef.uriWithid(client.getId()), jimmyToken, client2, FORBIDDEN);

    }
}