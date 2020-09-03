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

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.EventDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.EventUsersDef;
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
class EventResourceTest {

    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020,6,24,9,0);
    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020,6,24,17,0);
    private static final LocalDateTime THE_28_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020,6,28,17,0);
    private static final String EVENT_NAME = "The test event";
    private static final String EVENT_DESCRIPTION = "It's a corporate event";

    @Inject
    Flyway flyway;

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldCreateUserEventWhenCreateEventTemplate(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        create(getUserAccessToken());
        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);
        //THEN: userEvent of attendees are created
        EventTemplateResponse expectedResponse = generateExpectedEventTemplateResponse(eventName, 1L);

        EventTemplateResponse actual = Arrays.asList(getValidation(EventDef.uri, samToken).extract().as(EventTemplateResponse[].class)).get(0);
        assertThat(timeKeeperTestUtils.toJson(actual), is(timeKeeperTestUtils.toJson(expectedResponse)));

        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), samToken)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());

        assertFalse(sortedUserResponses.isEmpty());
        assertEquals(1, sortedUserResponses.size());
        assertThat(sortedUserResponses.get(0).getEmail(), is(sam.getEmail()));
        assertThat(sortedUserResponses.get(0).getId(), is(sam.getId()));
    }

    @Test
    void shouldListEventTemplates(){
        //WITH: Unique EventName
        final String eventName1 = generateRandomEventName();
        final String eventName2 = generateRandomEventName();

        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        var sam = create(adminToken);
        var jimmy = create(getUserAccessToken());
        //WHEN: 2 eventTemplates are created
        EventTemplateRequest eventTemplateRequest1 = generateTestEventRequest(eventName1,1L);
        EventTemplateRequest eventTemplateRequest2 = generateTestEventRequest(eventName2, 2L);
        create(eventTemplateRequest1,adminToken);
        create(eventTemplateRequest2,adminToken);
        //THEN: get on /events return the 2 event template
        EventTemplateResponse expectedResponse1 = generateExpectedEventTemplateResponse(eventName1,1L);
        EventTemplateResponse expectedResponse2 = generateExpectedEventTemplateResponse(eventName2,2L);
        getValidation(EventDef.uri, adminToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse1,expectedResponse2))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldListUserOfAnEvent(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        UserResponse userResponseAdmin = create(adminToken);
        UserResponse userResponseJimmy = create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 2 attendees
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, userResponseAdmin.getId(), userResponseJimmy.getId());
        create(newEventTemplate,adminToken);
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
    void shouldUpdateUserEventDateWhenUpdateEventTemplate(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();
        //GIVEN: 1 user and an existing event with this user as attendee
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        final String jimmyToken = getUserAccessToken();
        UserResponse jimmy = create(jimmyToken);

        create(generateTestEventRequest(eventName, userSam.getId()),adminToken);
        //WHEN the event date are updated
        LocalDateTime updatedStartTime =  LocalDateTime.of(2020,6,28,9,0);
        LocalDateTime updatedEndTime =  LocalDateTime.of(2020,6,28,15,0);
        EventTemplateRequest updatedRequest = new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                updatedStartTime,
                updatedEndTime,
                Collections.singletonList(new EventTemplateRequest.UserEventRequest(jimmy.getId()))
        );
        update(updatedRequest,EventDef.uriPlusId(1L),adminToken);
        //THEN if we set jimmy and we remove sam, we should keep only Jimmy
        EventTemplateResponse expectedResponse = new EventTemplateResponse(
                1L,
                eventName,
                EVENT_DESCRIPTION,
                updatedStartTime,
                updatedEndTime
        );
        getValidation(EventDef.uri, adminToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse))).statusCode(CoreMatchers.is(OK.getStatusCode()));

        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), adminToken)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());

        assertFalse(sortedUserResponses.isEmpty());
        assertEquals(1, sortedUserResponses.size()); // Sam userEvent should be deleted and we should have only jimmy
        assertThat(sortedUserResponses.get(0).getEmail(), is(jimmy.getEmail())); // TODO Jimmy
        assertThat(sortedUserResponses.get(0).getId(), is(jimmy.getId()));
    }

    @Test
    void shouldUpdateUserEventListWhenUpdateEventTemplate(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();
        //GIVEN: 2 user and an existing event with 1 attendee (Sam)
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);
        UserResponse jimmy = create(getUserAccessToken());
        create(generateTestEventRequest(eventName, userSam.getId()),adminToken);
        //WHEN the event is updated with another attendee
        EventTemplateRequest updatedRequest = new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Collections.singletonList(new EventTemplateRequest.UserEventRequest(jimmy.getId()))
        );
        update(updatedRequest,EventDef.uriPlusId(1L),adminToken);
        //THEN the userEvent list contains this new attendee (Jimmy)
        UserResponse expectedResult = new UserResponse(
                jimmy.getId(),
                jimmy.getName(),
                jimmy.getEmail(),
                jimmy.getPicture(),
                jimmy.getProfiles(),
                Collections.EMPTY_LIST
        );

        EventTemplateResponse expectedResponse = new EventTemplateResponse(
                1L,
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM
        );
        getValidation(EventDef.uri, adminToken).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse))).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }


    @Test
    void createDuplicatedEvent(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 1 attendee
        final EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, 1L);
        create(newEventTemplate,adminToken);

        //WHEN: Create event for second time with same template request
        postValidation(EventDef.uri, adminToken, newEventTemplate).statusCode(CoreMatchers.is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    void updateDuplicatedEvent(){
        //WITH: Unique EventName
        final String eventName = generateRandomEventName();
        //GIVEN: Admin user
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 1 attendee
        final EventTemplateRequest firstEventTemplate = generateTestEventRequest(eventName, 1L);
        create(firstEventTemplate, adminToken);

        final EventTemplateRequest secondEventTemplate = new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_28_TH_JUNE_2020_AT_5_PM,
                        Stream.of(1L)
                        .map(EventTemplateRequest.UserEventRequest::new)
                        .collect(Collectors.toList())
        );
        final Long secondEventId = create(secondEventTemplate, adminToken).getId();

        //WHEN: Update an event with already existing event
        putValidation(EventDef.uriPlusId(secondEventId), adminToken, firstEventTemplate).statusCode(CoreMatchers.is(BAD_REQUEST.getStatusCode()));
    }

    private EventTemplateRequest generateTestEventRequest(String eventName, Long... usersId){
        return new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Arrays.stream(usersId)
                        .sorted(Comparator.comparingLong(value -> (long)value))
                        .map(EventTemplateRequest.UserEventRequest::new)
                        .collect(Collectors.toList())
        );
    }

    // the backend reverse order of the user ID and the generated userEvent id
    private EventTemplateResponse generateExpectedEventTemplateResponse(String eventName, Long expectedID){
        return new EventTemplateResponse(
                expectedID,
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM
        );
    }

    private String generateRandomEventName() {
        final int length = 5;
        final boolean useLetters = true;
        final boolean useNumbers = false;
        return EVENT_NAME + "-" + RandomStringUtils.random(length, useLetters, useNumbers);
    }

}