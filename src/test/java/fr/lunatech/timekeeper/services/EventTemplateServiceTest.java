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

package fr.lunatech.timekeeper.services;

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import fr.lunatech.timekeeper.resources.utils.DataTestProvider;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fr.lunatech.timekeeper.resources.utils.DataTestProvider.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getUserAccessToken;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest

@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class EventTemplateServiceTest {

    private static final LocalDateTime START_DAY = LocalDateTime.of(2020, 1, 1, 8, 0);
    @Inject
    Flyway flyway;
    @Inject
    EventTemplateService eventTemplateService;
    @Inject
    DataTestProvider dataTestProvider;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void create_should_create_an_user_event() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final String eventName = dataTestProvider.generateRandomEventName();
        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest(eventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());

        final Organization organization = dataTestProvider.generateOrganization();
        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        List<EventTemplateResponse> eventsBefore = eventTemplateService.listEventCompany(ctx);
        eventTemplateService.create(eventTemplateRequest, ctx);
        List<EventTemplateResponse> eventsAfter = eventTemplateService.listEventCompany(ctx);

        Assertions.assertTrue(eventsBefore.isEmpty());
        Assertions.assertEquals(1, eventsAfter.size());
    }

    @Test
    void should_reject_create_event_without_startdatetime() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event with no startDateTime", null, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        assertThrows(CreateResourceException.class, () -> eventTemplateService.create(eventTemplateRequest, ctx), "should throw a CreateException if the startDateTime is null");

    }

    @Test
    void should_reject_create_event_without_enddatetime() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event with no endDateTime", THE_18_TH_JULY_2020_AT_10_AM, null, sam.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        assertThrows(CreateResourceException.class, () -> eventTemplateService.create(eventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }

    @Test
    void should_reject_create_event_without_enddatetime_before_startdatetime() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event with no startDateTime", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.minusHours(2), sam.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        assertThrows(CreateResourceException.class, () -> eventTemplateService.create(eventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }

    @Test
    void create_should_update_an_user_event() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);


        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());


        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);

        assertTrue(maybeEventId.isPresent());

        EventTemplateRequest updatedEventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("updated name", THE_18_TH_JULY_2020_AT_10_AM.plusDays(1), THE_18_TH_JULY_2020_AT_10_AM.plusDays(1).plusHours(6));

        eventTemplateService.update(maybeEventId.get(), updatedEventTemplateRequest, ctx);

        var maybeUpdatedEvent = eventTemplateService.getById(maybeEventId.get(), ctx);
        assertTrue(maybeUpdatedEvent.isPresent());

        var updatedEvent = maybeUpdatedEvent.get();

        assertEquals("updated name", updatedEvent.getName());
        assertEquals(updatedEventTemplateRequest.getStartDateTime(), updatedEvent.getStartDateTime());
        assertEquals(updatedEventTemplateRequest.getEndDateTime(), updatedEvent.getEndDateTime());
        assertTrue(updatedEvent.getAttendees().isEmpty());
    }

    @Test
    void create_should_not_update_if_start_date_is_null() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);

        assertTrue(maybeEventId.isPresent());

        EventTemplateRequest updatedEventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("update an event with no startDateTime", null, THE_18_TH_JULY_2020_AT_10_AM.plusDays(1).plusHours(6), sam.getId());

        Long eventId = maybeEventId.get();
        assertThrows(UpdateResourceException.class, () -> eventTemplateService.update(eventId, updatedEventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }

    @Test
    void create_should_not_update_if_end_date_is_null() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());


        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);

        assertTrue(maybeEventId.isPresent());

        EventTemplateRequest updatedEventTemplateRequest = dataTestProvider.generateEventTemplateRequest("an event with no endDateTime", THE_18_TH_JULY_2020_AT_10_AM, null, sam.getId());

        Long eventId = maybeEventId.get();
        assertThrows(UpdateResourceException.class, () -> eventTemplateService.update(eventId, updatedEventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }

    @Test
    void create_should_not_update_if_end_date_is_beofre_start_date() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), sam.getId());


        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);

        assertTrue(maybeEventId.isPresent());

        final EventTemplateRequest updatedEventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("an event", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.minusHours(6), sam.getId());

        Long eventId = maybeEventId.get();
        assertThrows(UpdateResourceException.class, () -> eventTemplateService.update(eventId, updatedEventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }

    @Test
    void create_should_not_create_overlapping_events_for_same_user_tk_441() {
        // Given an Event with Jimmy
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final String jimmyToken = getUserAccessToken();
        var jimmy = create(jimmyToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("Agira", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), jimmy.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);
        assertTrue(maybeEventId.isPresent());

        // WHEN jimmy is added to another eventTemplate but the new date overlaps each other
        final EventTemplateRequest anotherEvent =
                dataTestProvider.generateEventTemplateRequest("Hackbreakfast", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(3), jimmy.getId());


        assertEquals(Optional.of(2L), eventTemplateService.create(anotherEvent, ctx), "Should create a 2nd userEvent");

        // THEN
        Optional<EventTemplateResponse> response = eventTemplateService.getById(2L, ctx);
        assertTrue(response.isPresent());
        assertTrue(response.get().getAttendees().isEmpty(), "Jimmy should not be a participant of the 2nd event");
    }

    @Test
    void createEvent_should_add_the_user_to_another_event() {
        // Given an Event with Jimmy
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        final String jimmyToken = getUserAccessToken();
        var jimmy = create(jimmyToken);

        final EventTemplateRequest eventTemplateRequest =
                dataTestProvider.generateEventTemplateRequest("Agira", THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_10_AM.plusHours(6), jimmy.getId());

        final Organization organization = dataTestProvider.generateOrganization();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        var maybeEventId = eventTemplateService.create(eventTemplateRequest, ctx);
        assertTrue(maybeEventId.isPresent());

        // WHEN jimmy is added to another eventTemplate but the new date overlaps each other
        final EventTemplateRequest anotherEvent =
                dataTestProvider.generateEventTemplateRequest("Hackbreakfast", THE_24_TH_JUNE_2020_AT_8_AM, THE_24_TH_JUNE_2020_AT_2_PM.plusHours(3), jimmy.getId());


        assertEquals(Optional.of(2L), eventTemplateService.create(anotherEvent, ctx), "Should create a 2nd userEvent");

        // THEN
        Optional<EventTemplateResponse> response = eventTemplateService.getById(2L, ctx);
        assertTrue(response.isPresent());
        assertFalse(response.get().getAttendees().isEmpty(), "Jimmy should be an attendee of the 2nd event");
    }
}
