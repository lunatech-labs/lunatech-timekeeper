package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.EventDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.EventUsersDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class EventResourceTest {

    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020,6,24,9,0);
    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020,6,24,17,0);
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
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(2L);
        create(newEventTemplate,adminToken);
        //THEN: userEvent of attendees are created
        EventTemplateResponse expectedResponse = generateExpectedEventTemplateResponse(1L,1L);
        getValidation(EventDef.uri, adminToken, Response.Status.OK).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse)));
    }

    @Test
    void shouldListEventTemplates(){
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(getUserAccessToken());
        //WHEN: 2 eventTemplates are created
        EventTemplateRequest eventTemplateRequest1 = generateTestEventRequest(1L);
        EventTemplateRequest eventTemplateRequest2 = generateTestEventRequest(2L);
        create(eventTemplateRequest1,adminToken);
        create(eventTemplateRequest2,adminToken);
        //THEN: get on /events return the 2 event template
        EventTemplateResponse expectedResponse1 = generateExpectedEventTemplateResponse(1L,1L);
        EventTemplateResponse expectedResponse2 = generateExpectedEventTemplateResponse(2L,2L);
        getValidation(EventDef.uri, adminToken, Response.Status.OK).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse1,expectedResponse2)));

    }

    @Test
    void shouldListUserOfAnEvent(){
        //GIVEN: 2 user
        final String adminToken = getAdminAccessToken();
        UserResponse userResponseAdmin = create(adminToken);
        UserResponse userResponseJimmy = create(getUserAccessToken());
        //WHEN: an eventTemplate is created with the 2 attendees
        EventTemplateRequest newEventTemplate = generateTestEventRequest(1L,2L);
        create(newEventTemplate,adminToken);
        //THEN: the attendees list is returned
        List<UserResponse> sortedUserResponses = Arrays.stream(
                getValidation(EventUsersDef.uriWithMultiId(1L), adminToken, Response.Status.OK)
                        .extract()
                        .as(UserResponse[].class))
                .sorted(Comparator.comparingLong(UserResponse::getId))
                .collect(Collectors.toList());
        MatcherAssert.assertThat(timeKeeperTestUtils.toJson(sortedUserResponses), is(timeKeeperTestUtils.listOfTasJson(userResponseAdmin, userResponseJimmy)));
    }

    @Test
    void shouldUpdateUserEventDateWhenUpdateEventTemplate(){
        //GIVEN: 1 user and an existing event with this user as attendee
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(generateTestEventRequest(1L),adminToken);
        //WHEN the event date are updated
        LocalDateTime updatedStartTime =  LocalDateTime.of(2020,6,28,9,0);
        LocalDateTime updatedEndTime =  LocalDateTime.of(2020,6,28,15,0);
        EventTemplateRequest updatedRequest = new EventTemplateRequest(
                EVENT_NAME,
                EVENT_DESCRIPTION,
                updatedStartTime,
                updatedEndTime,
                Collections.singletonList(new EventTemplateRequest.UserEventRequest(1L))
        );
        update(updatedRequest,EventDef.uriWithid(1L),adminToken);
        //THEN the userEvent are updated too with the new date
        EventTemplateResponse.UserEventResponse expectedUserEventResponse = new EventTemplateResponse.UserEventResponse(
                1L,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                EventType.COMPANY,
                updatedStartTime,
                updatedEndTime
        );
        EventTemplateResponse expectedResponse = new EventTemplateResponse(
                1L,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                updatedStartTime,
                updatedEndTime,
                Collections.singletonList(expectedUserEventResponse)
        );
        getValidation(EventDef.uri, adminToken, Response.Status.OK).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse)));
    }

    @Test
    void shouldUpdateUserEventListWhenUpdateEventTemplate(){
        //GIVEN: 2 user and an existing event with 1 attendee
        final String adminToken = getAdminAccessToken();
        create(adminToken);
        create(getUserAccessToken());
        create(generateTestEventRequest(1L),adminToken);
        //WHEN the event is updated with another attendee
        EventTemplateRequest updatedRequest = new EventTemplateRequest(
                EVENT_NAME,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Collections.singletonList(new EventTemplateRequest.UserEventRequest(2L))
        );
        update(updatedRequest,EventDef.uriWithid(1L),adminToken);
        //THEN the userEvent list contains this new attendee
        EventTemplateResponse.UserEventResponse expectedUserEventResponse = new EventTemplateResponse.UserEventResponse(
                2L,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                EventType.COMPANY,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM
        );
        EventTemplateResponse expectedResponse = new EventTemplateResponse(
                1L,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Collections.singletonList(expectedUserEventResponse)
        );
        getValidation(EventDef.uri, adminToken, Response.Status.OK).body(is(timeKeeperTestUtils.listOfTasJson(expectedResponse)));
    }


    private EventTemplateRequest generateTestEventRequest(Long... usersId){
        return new EventTemplateRequest(
                EVENT_NAME,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Arrays.stream(usersId)
                        .sorted(Comparator.comparingLong(value -> value))
                        .map(EventTemplateRequest.UserEventRequest::new)
                        .collect(Collectors.toList())
        );
    }

    private EventTemplateResponse generateExpectedEventTemplateResponse(Long expectedID, Long... userIDs){
        return new EventTemplateResponse(
                expectedID,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Arrays.stream(userIDs)
                        .sorted(Comparator.comparingLong(value -> value))
                        .map(this::generateExpectedUserEvent)
                        .collect(Collectors.toList())
        );
    }

    private EventTemplateResponse.UserEventResponse generateExpectedUserEvent(Long id){
        return new EventTemplateResponse.UserEventResponse(
                id,
                EVENT_NAME,
                EVENT_DESCRIPTION,
                EventType.COMPANY,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM);
    }


}