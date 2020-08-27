package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


class UserEventServiceTest {

    @Test
    void shouldBeTrueForEventWithDurationUnderAWeekDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 12, 2020, TimeKeeperDateUtils::getWeekNumberFromDate));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperThanAWeekDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,26,17,0,0);

        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 11, 2020, TimeKeeperDateUtils::getWeekNumberFromDate));
        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 12, 2020, TimeKeeperDateUtils::getWeekNumberFromDate));
        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 13, 2020, TimeKeeperDateUtils::getWeekNumberFromDate));
    }

    @Test
    void shouldBeFalseForEventNotIncludedInWeekDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,13,17,0,0);

        assertFalse(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 13, 2020, TimeKeeperDateUtils::getWeekNumberFromDate));
    }

    @Test
    void shouldBeTrueForEventWithDurationUnderAMonthDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 3, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperThanAMonthDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,05,1,17,0,0);

        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 2, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 3, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 4, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
        assertTrue(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 5, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
    }

    @Test
    void shouldBeFalseForEventNotIncludedInTheMonthDuration() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertFalse(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 3, 2020, TimeKeeperDateUtils::getMonthNumberFromDate));
    }

    @Test
    void shouldBeFalseForEventInTheWrongYear() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertFalse(userEventService.validateYear(userEvent, 2021));
        assertFalse(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 02, 2021, TimeKeeperDateUtils::getMonthNumberFromDate));
        assertFalse(userEventService.isUserEventInWeekOrMonthNumber(userEvent, 05, 2021, TimeKeeperDateUtils::getMonthNumberFromDate));
    }

    @Test
    void shouldBeTrueForEventInTheRightYear() {
        UserEventService userEventService = new UserEventService();
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertTrue(userEventService.validateYear(userEvent, 2020));
    }
}