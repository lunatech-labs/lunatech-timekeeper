/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

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
@Tag("integration")
class UserEventTest {

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
    void shouldNotDeleteUserEventIfAttendeesIsDeleted() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient UET", "A Client created by an admin user"), adminToken);

        final var project = create(new ProjectRequest("Some Project", true, "A project can be created by a user also", client.getId(), true, emptyList(), 1L), userAccessToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), userAccessToken).body(is(timeKeeperTestUtils.toJson(project))).statusCode(is(OK.getStatusCode()));

        // We use Panache directly to manipulate and test the DB
        Organization organisation = Organization.findAll().firstResult();
        assertNotNull(organisation);

        User user = User.findById(1L);
        assertNotNull(user);

        // Let's create a UserEvent directly, just to write something in the DB
        final UserEvent userEvent = new UserEvent();
        userEvent.id = null;
        userEvent.eventType = EventType.COMPANY;
        userEvent.name = "Hackbreakfast";
        userEvent.description = "A great company event";
        userEvent.startDateTime = LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
        userEvent.endDateTime = LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS);
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
        User.deleteAll(); // `user` instance is a detached Hibernate obj and cannot be used
        transaction.commit();

        // Check that the userEvent still exists and can be retrieved from the DB.
        // This is really important : we should not delete userEvent if a user gets deleted for any reason.
        transaction.begin();
        UserEvent userEventFromDBAfterUserDelete = UserEvent.findById(1L);
        transaction.commit();

        assertNotNull(userEventFromDBAfterUserDelete);
        assertNull(userEventFromDBAfterUserDelete.owner);
        assertEquals(1L, userEventFromDBAfterUserDelete.id);
        assertEquals("Hackbreakfast", userEventFromDBAfterUserDelete.name);
    }

    @Test
    void shouldNotDeleteUserEventIfAssociatedEventTemplateIsDeleted() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        final String adminToken = getAdminAccessToken();
        final String userAccessToken = getUserAccessToken();

        final var client = create(new ClientRequest("NewClient UET2", "One super client for test"), adminToken);
        final var project = create(new ProjectRequest("Some Project 2", true, "Test 1,2,1,2", client.getId(), true, emptyList(), 1L), userAccessToken);

        getValidation(ProjectDef.uriPlusId(project.getId()), userAccessToken).body(is(timeKeeperTestUtils.toJson(project))).statusCode(is(OK.getStatusCode()));

        // We use Panache directly to manipulate and test the DB
        Organization organisation = Organization.findAll().firstResult();
        assertNotNull(organisation);

        User user = User.findById(1L);
        assertNotNull(user);

        // Create an EventTemplate with the user as attendees
        final EventTemplate hackBreakfastTemplate = new EventTemplate();
        hackBreakfastTemplate.id = null;
        hackBreakfastTemplate.organization = organisation;
        hackBreakfastTemplate.name = "Hackbreakfast2";
        hackBreakfastTemplate.description = "A great company event";
        hackBreakfastTemplate.startDateTime = LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
        hackBreakfastTemplate.endDateTime = LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS);

        final UserEvent userEvent = new UserEvent();
        userEvent.id = null;
        userEvent.eventType = EventType.COMPANY;
        userEvent.name = hackBreakfastTemplate.name;
        userEvent.description = "A great company event";
        userEvent.startDateTime = LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
        userEvent.endDateTime = LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS);
        userEvent.owner = user;
        userEvent.eventTemplate = hackBreakfastTemplate;

        transaction.begin();
        hackBreakfastTemplate.persistAndFlush();
        transaction.commit();

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

        // Check that the userEvent is not deleted if we delete the EventTemplate (Note : this is arguable...)
        transaction.begin();
        UserEvent userEventFromDBAfterDelete = UserEvent.findById(1L);
        transaction.commit();

        assertNotNull(userEventFromDBAfterDelete);
        assertEquals(1L, userEventFromDBAfterDelete.id);
        assertEquals("Hackbreakfast2", userEventFromDBAfterDelete.name);
        assertNull(userEventFromDBAfterDelete.eventTemplate);
    }
}
