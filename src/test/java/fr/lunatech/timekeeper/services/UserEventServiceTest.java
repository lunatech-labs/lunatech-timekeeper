package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEventServiceTest {

    private final UserEventService userEventService = new UserEventService();

    @Test
    void shouldBeTrueForEventWithDurationUnderAWeekDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.isUserEventInWeekNumber(userEvent, 12, 2020));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperThanAWeekDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,26,17,0,0);

        assertTrue(userEventService.isUserEventInWeekNumber(userEvent, 11, 2020));
        assertTrue(userEventService.isUserEventInWeekNumber(userEvent, 12, 2020));
        assertTrue(userEventService.isUserEventInWeekNumber(userEvent, 13, 2020));
    }

    @Test
    void shouldBeFalseForEventNotIncludedInWeekDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,13,17,0,0);

        assertFalse(userEventService.isUserEventInWeekNumber(userEvent, 13, 2020));
    }

    @Test
    void shouldBeTrueForEventWithDurationUnderAMonthDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.isUserEventInMonthNumber(userEvent, 3, 2020));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperThanAMonthDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,05,1,17,0,0);

        assertTrue(userEventService.isUserEventInMonthNumber(userEvent, 2, 2020));
        assertTrue(userEventService.isUserEventInMonthNumber(userEvent, 3, 2020));
        assertTrue(userEventService.isUserEventInMonthNumber(userEvent, 4, 2020));
        assertTrue(userEventService.isUserEventInMonthNumber(userEvent, 5, 2020));
    }

    @Test
    void shouldBeFalseForEventNotIncludedInTheMonthDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertFalse(userEventService.isUserEventInMonthNumber(userEvent, 3, 2020));
    }

    @Test
    void shouldBeFalseForEventInTheWrongYear() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertFalse(userEventService.validateYear(userEvent, 2021));
        assertFalse(userEventService.isUserEventInMonthNumber(userEvent, 02, 2021));
        assertFalse(userEventService.isUserEventInWeekNumber(userEvent, 05, 2021));
    }

    @Test
    void shouldBeTrueForEventInTheRightYear() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,02,1,17,0,0);

        assertTrue(userEventService.validateYear(userEvent, 2020));
    }
}