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
import fr.lunatech.timekeeper.resources.KeycloakTestResource;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
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

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class EventTemplateServiceTest {

    @Inject
    Flyway flyway;

    @Inject
    EventTemplateService eventTemplateService;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    private static final LocalDateTime START_DAY = LocalDateTime.of(2020, 1, 1, 8, 0);

    @Test
    void create_should_create_an_user_event() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "request1",
                "description1",
                START_DAY,
                START_DAY.plusHours(6),
                Lists.newArrayList(userEventRequest)
        );

        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        List<EventTemplateResponse> eventsBefore = eventTemplateService.listAll(ctx);
        eventTemplateService.create(eventTemplateRequest, ctx);
        List<EventTemplateResponse> eventsAfter = eventTemplateService.listAll(ctx);

        Assertions.assertTrue(eventsBefore.isEmpty());
        Assertions.assertEquals(1, eventsAfter.size());
    }

    @Test
    void should_reject_create_event_without_startdatetime() {
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "an event with no startDateTime",
                "description1",
                null,
                START_DAY.plusHours(6),
                Lists.newArrayList(userEventRequest)
        );
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

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

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "same name",
                "description1",
                START_DAY,
                null,
                Lists.newArrayList(userEventRequest)
        );
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

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

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "same name",
                "description1",
                START_DAY,
                START_DAY.minusHours(2),
                Lists.newArrayList(userEventRequest)
        );
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

        AuthenticationContext ctx = new AuthenticationContext(
                sam.getId(),
                organization,
                Collections.emptyList()
        );

        assertThrows(CreateResourceException.class, () -> eventTemplateService.create(eventTemplateRequest, ctx), "should throw a CreateException if the endDateTime is null");
    }
}
