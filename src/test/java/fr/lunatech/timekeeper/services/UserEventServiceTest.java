package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserEventServiceTest {

    UserEventService userEventService = new UserEventService();

    @Test
    void shouldBeTrueForEventWithDurationUnderAWeekDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.getUserEventForWeekNumber(userEvent, 12));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAWeekDurationAndIsInTheMiddle() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,26,17,0,0);

        assertTrue(userEventService.getUserEventForWeekNumber(userEvent, 12));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAWeekDurationAndIsAtTheStart() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,26,17,0,0);

        assertTrue(userEventService.getUserEventForWeekNumber(userEvent, 11));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAWeekDurationAndIsAtTheEnd() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,26,17,0,0);

        assertTrue(userEventService.getUserEventForWeekNumber(userEvent, 13));
    }

    @Test
    void shouldBeFalseForEventOutOfWeekRange() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,13,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,13,17,0,0);

        assertFalse(userEventService.getUserEventForWeekNumber(userEvent, 13));
    }

    @Test
    void shouldBeTrueForEventWithDurationUnderAMonthDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);

        assertTrue(userEventService.getUserEventForMonthNumber(userEvent, 3));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAMonthDuration() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,05,1,17,0,0);

        assertTrue(userEventService.getUserEventForMonthNumber(userEvent, 4));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAMonthDurationAndIsAtTheStart() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,05,1,17,0,0);

        assertTrue(userEventService.getUserEventForMonthNumber(userEvent, 2));
    }

    @Test
    void shouldBeTrueForEventWithDurationUpperAMonthDurationAndIsAtTheEnd() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,02,1,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,05,1,17,0,0);

        assertTrue(userEventService.getUserEventForMonthNumber(userEvent, 5));
    }

}