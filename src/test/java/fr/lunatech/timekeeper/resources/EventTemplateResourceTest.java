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

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.DataEventProvider;
import fr.lunatech.timekeeper.resources.utils.HttpTestRuntimeException;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.Tuple2;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.DataEventProvider.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.EventUsersDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.TemplateEventDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.*;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class EventTemplateResourceTest {

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @Inject
    DataEventProvider eventProvider;

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldNotCreateEventWithSameNameAndSameDates() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        final var newEventTemplate =
                eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM);
        create(newEventTemplate, samToken);

        //THEN: if we try to create an event with same name, same date, it should NOT work
        final var anotherEventTemplate =
                eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_8_AM, THE_24_TH_JUNE_2020_AT_2_PM);
        try {
            create(anotherEventTemplate, samToken);
        } catch (HttpTestRuntimeException httpError) {
            assertEquals("application/json", httpError.getMimeType());
            assertEquals(400, httpError.getHttpStatus());
        }
    }

    @Test
    void shouldCreateUserEventWhenCreateEventTemplate() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        final var sam = create(samToken);

        create(getUserAccessToken());
        //WHEN: an eventTemplateRequest is created with SAM as an attendee

        final Tuple2<EventTemplateRequest, EventTemplateResponse> eventTemplate =
                eventProvider.generateEventTemplateTuple(
                        eventName,
                        THE_24_TH_JUNE_2020_AT_9_AM,
                        THE_24_TH_JUNE_2020_AT_5_PM,
                        1L,
                        sam.getId()
                );
        create(eventTemplate._1, samToken);

        //THEN: userEvent of attendees are created
        EventTemplateResponse expectedResponse = eventTemplate._2;

        Optional<EventTemplateResponse> actual = Arrays.asList(getValidation(TemplateEventDef.uri, samToken).extract().as(EventTemplateResponse[].class)).stream().findFirst();
        actual.ifPresent(et -> assertThat(timeKeeperTestUtils.toJson(et), is(timeKeeperTestUtils.toJson(expectedResponse))));

        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), samToken)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());

        assertFalse(sortedUserResponses.isEmpty());
        assertEquals(1, sortedUserResponses.size());
        sortedUserResponses.stream().findFirst().ifPresent(userResponse -> {
            assertThat(userResponse.getEmail(), is(sam.getEmail()));
            assertThat(userResponse.getId(), is(sam.getId()));
        });
    }

    @Test
    void shouldListEventTemplates() {
        //WITH: Unique EventName
        final String eventName1 = eventProvider.generateRandomEventName();
        final String eventName2 = eventProvider.generateRandomEventName();

        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        create(getUserAccessToken());
        //WHEN: 2 eventTemplates are created
        final Tuple2<EventTemplateRequest, EventTemplateResponse> eventTuple =
                eventProvider.generateEventTemplateTuple(eventName1, THE_24_TH_JUNE_2020_AT_9_AM, THE_28_TH_JUNE_2020_AT_5_PM, 1L, sam.getId());
        final Tuple2<EventTemplateRequest, EventTemplateResponse> eventTuple2 =
                eventProvider.generateEventTemplateTuple(eventName2, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, 2L, sam.getId());
        create(eventTuple._1, adminToken);
        create(eventTuple2._1, adminToken);

        //THEN: get on /events return the 2 event template
        getValidation(TemplateEventDef.uri, adminToken)
                .body(is(timeKeeperTestUtils.listOfTasJson(eventTuple._2, eventTuple2._2)))
                .statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldListUserOfAnEvent() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        final UserResponse userResponseAdmin = create(adminToken);
        final UserResponse userResponseJimmy = create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 2 attendees
        EventTemplateRequest newEventTemplate =
                eventProvider.generateEventTemplateRequest(
                        eventName,
                        THE_24_TH_JUNE_2020_AT_9_AM,
                        THE_24_TH_JUNE_2020_AT_5_PM,
                        userResponseAdmin.getId(),
                        userResponseJimmy.getId()
                );
        create(newEventTemplate, adminToken);
        //THEN: the attendees list is returned
        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), adminToken)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());
        assertThat(timeKeeperTestUtils.toJson(sortedUserResponses), is(timeKeeperTestUtils.listOfTasJson(userResponseAdmin, userResponseJimmy)));
    }

    @Test
    void shouldUpdateUserEventDateWhenUpdateEventTemplate() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();
        //GIVEN: 1 user and an existing event with this user as attendee
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        final String jimmyToken = getUserAccessToken();
        UserResponse jimmy = create(jimmyToken);

        create(eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, userSam.getId()), adminToken);

        //WHEN the event date are updated
        final Tuple2<EventTemplateRequest, EventTemplateResponse> updatedRequest =
                eventProvider.generateEventTemplateTuple(eventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, 1L, jimmy.getId());
        update(updatedRequest._1, TemplateEventDef.uriPlusId(1L), adminToken);

        //THEN if we set jimmy and we remove sam, we should keep only Jimmy
        final EventTemplateResponse expectedResponse = updatedRequest._2;
        getValidation(TemplateEventDef.uri, adminToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse))).statusCode(CoreMatchers.is(OK.getStatusCode()));

        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), adminToken)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());

        assertFalse(sortedUserResponses.isEmpty());
        assertEquals(1, sortedUserResponses.size()); // Sam userEvent should be deleted and we should have only jimmy
        sortedUserResponses.stream().findFirst().ifPresent(userResponse -> {
            assertThat(userResponse.getEmail(), is(jimmy.getEmail()));
            assertThat(userResponse.getId(), is(jimmy.getId()));
        });
    }

    @Test
    void shouldUpdateUserEventListWhenUpdateEventTemplate() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();
        //GIVEN: 2 user and an existing event with 1 attendee (Sam)
        final String adminToken = getAdminAccessToken();
        final UserResponse userSam = create(adminToken);
        final UserResponse jimmy = create(getUserAccessToken());

        create(eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, userSam.getId()), adminToken);

        //WHEN the event is updated with another attendee
        final var tuple = eventProvider.generateEventTemplateTuple(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, 1L, jimmy.getId());
        update(tuple._1, TemplateEventDef.uriPlusId(1L), adminToken);
        //THEN the userEvent list contains this new attendee (Jimmy)
        getValidation(TemplateEventDef.uri, adminToken)
                .body(is(timeKeeperTestUtils.listOfTasJson(tuple._2)))
                .statusCode(CoreMatchers.is(OK.getStatusCode()));
    }


    @Test
    void createDuplicatedEvent() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        var admin = create(adminToken);
        create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 1 attendee
        final EventTemplateRequest newEventTemplate =
                eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, admin.getId());
        create(newEventTemplate, adminToken);

        //WHEN: Create event for second time with same template request
        postValidation(TemplateEventDef.uri, adminToken, newEventTemplate)
                .statusCode(CoreMatchers.is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    void updateDuplicatedEvent() {
        //WITH: Unique EventName
        final String eventName = eventProvider.generateRandomEventName();
        //GIVEN: Admin user
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        var user = create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 1 attendee
        final EventTemplateRequest firstEventTemplate =
                eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, user.getId());
        create(firstEventTemplate, adminToken);

        final EventTemplateRequest secondEventTemplate =
                eventProvider.generateEventTemplateRequest(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_28_TH_JUNE_2020_AT_5_PM, user.getId());
        final Long secondEventId = create(secondEventTemplate, adminToken).getId();

        //WHEN: Update an event with already existing event
        putValidation(TemplateEventDef.uriPlusId(secondEventId), adminToken, firstEventTemplate)
                .statusCode(CoreMatchers.is(BAD_REQUEST.getStatusCode()));
    }

}