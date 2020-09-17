package fr.lunatech.timekeeper.services;


import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getUserAccessToken;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;

import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest

@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class UserEventServiceIntegrationTest {

    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020,6,24,9,0);
    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020,6,24,17,0);
    private static final String EVENT_DESCRIPTION = "It's a corporate event";

    @Inject
    Flyway flyway;

    @Inject
    UserEventService userEventService;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
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
    @Test
    void shouldGetEventsByUserForAWeekNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest("Event Name 1", 1L);
        create(newEventTemplate,adminToken);
        var expected = userEventService.getEventsByUser(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020, TimeKeeperDateUtils::getWeekNumberFromDate);

        assertEquals(expected,userEventService.getEventsByUserForWeekNumber(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020));
    }

    @Test
    void shouldGetEventsByUserForAMonthNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest("Event Name 2", 1L);
        create(newEventTemplate,adminToken);
        var expected = userEventService.getEventsByUser(userSam.getId(), TimeKeeperDateUtils.getMonthNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020, TimeKeeperDateUtils::getMonthNumberFromDate);

        assertEquals(expected,userEventService.getEventsByUserForMonthNumber(userSam.getId(), TimeKeeperDateUtils.getMonthNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020));
    }

    @Test
    void shouldGetAnEmptyListOfEventsByUserForAWrongWeekNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest("Event Name 3", 1L);
        create(newEventTemplate,adminToken);
        var expected = List.of();

        assertEquals(expected,userEventService.getEventsByUserForWeekNumber(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()) + 1, 2020));
    }

    @Test
    void shouldGetAnEmptyListOfEventsByUserForAWrongMonthNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest("Event Name 4", 1L);
        create(newEventTemplate,adminToken);
        var expected = List.of();
        assertEquals(expected,userEventService.getEventsByUserForMonthNumber(userSam.getId(), TimeKeeperDateUtils.getMonthNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()) + 1, 2020));
    }


    @Test
    void shouldReturnTrueForTwoSeparateEvents() {
        //WITH: Unique EventName
        final String eventName = RandomStringUtils.randomAlphabetic(15);

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);

        //THEN isUserAvailableForDates return true for dates that are not overlapping the test event
        LocalDateTime dateBefore = newEventTemplate.getStartDateTime().minusDays(1);
        LocalDateTime endDateBefore = newEventTemplate.getEndDateTime().minusDays(1);
        assertTrue(userEventService.isUserAvailableForDates(sam.getId(),dateBefore,endDateBefore));
    }

    @Test
    void shouldReturnTrueForTwoSeparateEvents2() {
        //WITH: Unique EventName
        final String eventName = RandomStringUtils.randomAlphabetic(15);

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);

        //THEN isUserAvailableForDates return true for dates that are not overlapping the test event
        LocalDateTime dateAfter = newEventTemplate.getStartDateTime().plusDays(1);
        LocalDateTime endDateAfter = newEventTemplate.getEndDateTime().plusDays(1);
        assertTrue(userEventService.isUserAvailableForDates(sam.getId(),dateAfter,endDateAfter));
    }

    @Test
    void shouldReturnFalseForStartDateIdenticalButDifferentEndDate() {
        //WITH: Unique EventName
        final String eventName = RandomStringUtils.randomAlphabetic(15);

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);

        //THEN isUserAvailableForDates returns true as the endDate is just one hour after the previous event
        LocalDateTime dateAfter = newEventTemplate.getStartDateTime();
        LocalDateTime endDateAfter = newEventTemplate.getEndDateTime().minusHours(1);
        assertFalse(userEventService.isUserAvailableForDates(sam.getId(),dateAfter,endDateAfter));
    }

    @Test
    void shouldReturnFalseIfEvent2StartsBeforeAndEndBefore() {
        //WITH: Unique EventName
        final String eventName = RandomStringUtils.randomAlphabetic(15);

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);

        //THEN isUserAvailableForDates as the new Event start and end one hour before the event stored in DB
        LocalDateTime startBefore = newEventTemplate.getStartDateTime().minusHours(1);
        LocalDateTime endBefore = newEventTemplate.getEndDateTime().minusHours(1);
        assertFalse(userEventService.isUserAvailableForDates(sam.getId(),startBefore,endBefore));
    }

    @Test
    void shouldReturnFalseIfNewEventStartsAfter() {
        //WITH: Unique EventName
        final String eventName = RandomStringUtils.randomAlphabetic(15);

        //GIVEN: 2 user
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);
        create(getUserAccessToken());

        //WHEN: an eventTemplateRequest is created with SAM as an attendee
        EventTemplateRequest newEventTemplate = generateTestEventRequest(eventName, sam.getId());
        create(newEventTemplate, samToken);

        //THEN isUserAvailableForDates as the new Event start and end one hour before the event stored in DB
        LocalDateTime startBefore = newEventTemplate.getStartDateTime().plusHours(1);
        LocalDateTime endBefore = newEventTemplate.getEndDateTime().plusHours(1);
        assertFalse(userEventService.isUserAvailableForDates(sam.getId(),startBefore,endBefore));
    }
}
