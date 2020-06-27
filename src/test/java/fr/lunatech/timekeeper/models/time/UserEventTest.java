package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.resources.KeycloakTestResource;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import javax.transaction.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.ProjectDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
public class UserEventTest {

    @Inject
    Flyway flyway;

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @Inject
    UserTransaction transaction;


    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void shouldNotDeleteUserEventIfAttendeesIsDeleted() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient UET", "Un client créé en tant qu'admin"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "un projet peut aussi etre créé par un user", client.getId(), true, emptyList()), userAccessToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), userAccessToken, OK).body(is(timeKeeperTestUtils.toJson(project)));

        // We use Panache directly to manipulate and test the DB
        Organization organisation = Organization.findAll().firstResult();
        assertNotNull(organisation);

        User user = User.findById(1L);
        assertNotNull(user);

        // Let's create a UserEvent directly, just to write something in the DB
        final UserEvent userEvent = new UserEvent();
        userEvent.id=null;
        userEvent.eventType = EventType.COMPANY;
        userEvent.name = "Hackbreakfast";
        userEvent.description = "A great company event";
        userEvent.startDateTime = LocalDate.of(2020, 06, 17).atTime(9, 00).truncatedTo(ChronoUnit.HOURS);
        userEvent.endDateTime = LocalDate.of(2020, 06, 17).atTime(12, 00).truncatedTo(ChronoUnit.HOURS);
        userEvent.owner = user;
        transaction.begin();
        userEvent.persistAndFlush();
        transaction.commit();

        // Retrieve the userEvent
        assertEquals(1, UserEvent.count());
        assertNotNull(UserEvent.findById(1L));
        UserEvent userFromDB = UserEvent.findById(1L);
        assertEquals(1, userFromDB.id);

        // Delete the User
        transaction.begin();
        User.delete("id=?1",1L); // `user` instance is a detached Hibernate obj and cannot be used
        transaction.commit();

        // Check that the userEvent still exists and can be retrieved from the DB.
        // This is really important : we should not delete userEvent if a user gets deleted for any reason.
        UserEvent userFromDBAfterDelete = UserEvent.findById(1L);
        assertNotNull(userFromDBAfterDelete);
        assertEquals(1L, userFromDB.id);
        assertEquals("Hackbreakfast", userFromDB.name);
    }

    @Test
    public void shouldDeleteUserEventIfAssociatedEventTemplateIsDeleted() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient UET2", "Un client"), adminToken);
        final var project = create(new ProjectRequest("Some Project 2", true, "un projet de test", client.getId(), true, emptyList()), userAccessToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), userAccessToken, OK).body(is(timeKeeperTestUtils.toJson(project)));

        // We use Panache directly to manipulate and test the DB
        Organization organisation = Organization.findAll().firstResult();
        assertNotNull(organisation);

        User user = User.findById(1L);
        assertNotNull(user);

        // Create an EventTemplate
        final EventTemplate hackBreakfastTemplate = new EventTemplate();
        hackBreakfastTemplate.id=null;
        hackBreakfastTemplate.organization = organisation;
        hackBreakfastTemplate.name = "Hackbreakfast2";
        hackBreakfastTemplate.description = "A great company event";
        hackBreakfastTemplate.startDateTime = LocalDate.of(2020, 06, 17).atTime(9, 00).truncatedTo(ChronoUnit.HOURS);
        hackBreakfastTemplate.endDateTime = LocalDate.of(2020, 06, 17).atTime(12, 00).truncatedTo(ChronoUnit.HOURS);

        transaction.begin();
        hackBreakfastTemplate.persistAndFlush();
        transaction.commit();

        // Let's create a UserEvent directly, just to write something in the DB
        final UserEvent userEvent = new UserEvent();
        userEvent.id = null;
        userEvent.eventType = EventType.COMPANY;
        userEvent.name = hackBreakfastTemplate.name;
        userEvent.description = "A great company event";
        userEvent.startDateTime = LocalDate.of(2020, 06, 17).atTime(9, 00).truncatedTo(ChronoUnit.HOURS);
        userEvent.endDateTime = LocalDate.of(2020, 06, 17).atTime(12, 00).truncatedTo(ChronoUnit.HOURS);
        userEvent.owner = user;
        userEvent.eventTemplate = hackBreakfastTemplate;

        transaction.begin();
        userEvent.persistAndFlush();
        transaction.commit();

        // Retrieve the userEvent
        transaction.begin();
        assertEquals(1, UserEvent.count());
        UserEvent userEventBefore = UserEvent.findById(1L);
        assertNotNull(userEventBefore);
        assertEquals(1L, userEventBefore.id);
        assertEquals("Hackbreakfast2", userEventBefore.name);
        assertNotNull(userEventBefore.eventTemplate);
        transaction.commit();

        // Delete the EventTemplate
        transaction.begin();
        EventTemplate.deleteAll();
        transaction.commit();

        // Check that the userEvent is deleted if we delete the EventTemplate (Note : this is arguable...)
        transaction.begin();
        UserEvent userEventFromDBAfterDelete = UserEvent.findById(1L);
        transaction.commit();

        assertNotNull(userEventFromDBAfterDelete);
        assertEquals(1L, userEventFromDBAfterDelete.id);
        assertEquals("Hackbreakfast2", userEventFromDBAfterDelete.name);
        assertNull(userEventFromDBAfterDelete.eventTemplate);
    }
}
