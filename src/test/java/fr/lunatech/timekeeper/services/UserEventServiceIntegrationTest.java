package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.resources.KeycloakTestResource;
import fr.lunatech.timekeeper.resources.utils.DataEventProvider;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class UserEventServiceIntegrationTest {

    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020, 6, 24, 9, 0);
    private static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020, 6, 24, 17, 0);
    private static final String EVENT_DESCRIPTION = "It's a corporate event";

    @Inject
    Flyway flyway;

    @Inject
    UserEventService userEventService;

    @Inject
    DataEventProvider dataEventProvider;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldGetEventsByUserForAWeekNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = dataEventProvider.generateEventTemplateRequest("Event Name", THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, userSam.getId());
        create(newEventTemplate, adminToken);
        var expected = userEventService.getEventsByUser(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020, TimeKeeperDateUtils::getWeekNumberFromDate);

        assertEquals(expected, userEventService.getEventsByUserForWeekNumber(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()), 2020));
    }

    @Test
    void shouldGetEventsByUserForAMonthNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate =
                dataEventProvider.generateEventTemplateRequest(
                        "Event Name",
                        THE_24_TH_JUNE_2020_AT_9_AM,
                        THE_24_TH_JUNE_2020_AT_5_PM,
                        userSam.getId()
                );
        create(newEventTemplate, adminToken);
        var expected = userEventService.getEventsByUser(
                userSam.getId(),
                TimeKeeperDateUtils.getMonthNumberFromDate(
                        THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()
                ), 2020, TimeKeeperDateUtils::getMonthNumberFromDate
        );
        final var actual = userEventService.getEventsByUserForMonthNumber(
                userSam.getId(),
                TimeKeeperDateUtils.getMonthNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()),
                2020
        );
        assertEquals(expected, actual);

    }

    @Test
    void shouldGetAnEmptyListOfEventsByUserForAWrongWeekNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = dataEventProvider.generateEventTemplateRequest("Event Name", THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, userSam.getId());
        create(newEventTemplate, adminToken);
        var expected = List.of();

        assertEquals(expected, userEventService.getEventsByUserForWeekNumber(userSam.getId(), TimeKeeperDateUtils.getWeekNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()) + 1, 2020));
    }

    @Test
    void shouldGetAnEmptyListOfEventsByUserForAWrongMonthNumber() {
        //Given
        final String adminToken = getAdminAccessToken();
        UserResponse userSam = create(adminToken);

        //WHEN: an eventTemplate is created with the 1 attendee
        EventTemplateRequest newEventTemplate = dataEventProvider.generateEventTemplateRequest("Event Name", THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, userSam.getId());
        create(newEventTemplate, adminToken);
        var expected = List.of();
        assertEquals(expected, userEventService.getEventsByUserForMonthNumber(userSam.getId(), TimeKeeperDateUtils.getMonthNumberFromDate(THE_24_TH_JUNE_2020_AT_9_AM.toLocalDate()) + 1, 2020));
    }

}
