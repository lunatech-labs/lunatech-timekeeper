package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEventResponseTest {


    @Test
    void shouldCreateFromUserEventWithNoStartDateTime() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = null;
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertNull(response.getStartDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("", response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTime() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020, 03, 16, 9, 0, 0);
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00", response.getStartDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("", response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithNoEndDateTime() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.endDateTime = null;
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertNull(response.getEndDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("", response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithEndDateTime() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 82L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.endDateTime = LocalDateTime.of(2020, 03, 16, 17, 0, 0);
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T17:00:00", response.getEndDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("", response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeInSameDay() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 96L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020, 03, 16, 9, 0, 0);
        userEvent.endDateTime = LocalDateTime.of(2020, 03, 16, 17, 0, 0);
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00", response.getStartDateTime());
        assertEquals("2020-03-16T17:00:00", response.getEndDateTime());
        assertEquals("PT8H", response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(), response.getEventUserDaysResponse());
        assertEquals("2020-03-16", response.getDate());
    }

    @Test
    void shouldCreateFromUserEventWithEventTypeCompany() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 1113L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.eventType = EventType.COMPANY;
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("COMPANY", response.getEventType());
    }

    @Test
    void shouldCreateFromUserEventWithEventTypePersonal() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 125L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.eventType = EventType.PERSONAL;
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("PERSONAL", response.getEventType());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeInTwoDifferentDays() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 137L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020, 03, 16, 9, 0, 0);
        userEvent.endDateTime = LocalDateTime.of(2020, 03, 17, 17, 0, 0);
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00", response.getStartDateTime());
        assertEquals("2020-03-17T17:00:00", response.getEndDateTime());
        assertEquals("PT32H", response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(), response.getEventUserDaysResponse());
        assertEquals("2020-03-16", response.getDate());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeWithAMiddleDay() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 154L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020, 3, 16, 9, 0, 0);
        userEvent.endDateTime = LocalDateTime.of(2020, 3, 18, 17, 0, 0);
        userEvent.owner = user;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00", response.getStartDateTime());
        assertEquals("2020-03-18T17:00:00", response.getEndDateTime());
        assertEquals("PT56H", response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(), response.getEventUserDaysResponse());
        assertEquals("2020-03-16", response.getDate());
    }

    @Test
    void shouldThrowAnExceptiontWithInvalidStartAndEndDates() {
        var userEvent = new UserEvent();
        var user = new User();
        user.id = 1L;
        userEvent.id = 171L;
        userEvent.name = "Event name with a startDate that is after the endDate (this is invalid)";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020, 10, 16, 9, 0, 0);
        userEvent.endDateTime = LocalDateTime.of(2019, 3, 18, 0, 0, 0);
        userEvent.owner = user;
        assertThrows(IllegalArgumentException.class, () -> UserEventResponse.createFromUserEvent(userEvent));
    }
}