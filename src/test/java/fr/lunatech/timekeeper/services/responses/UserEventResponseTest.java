package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEventResponseTest {

    @Test
    void shouldCreateFromUserEvent() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals(UserEventResponse.createFromUserEvent(userEvent),response);
    }

    @Test
    void shouldCreateFromUserEventWithEventUserDaysResponse() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals(UserEventResponse.createFromUserEvent(userEvent),response);
    }

    @Test
    void shouldCreateFromUserEventWithNoStartDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = null;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertNull(response.getStartDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("",response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00",response.getStartDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("",response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithNoEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.endDateTime = null;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertNull(response.getEndDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("",response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T17:00:00",response.getEndDateTime());
        assertNull(response.getEventUserDaysResponse());
        assertEquals("",response.getDuration());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeInSameDay() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,17,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00",response.getStartDateTime());
        assertEquals("2020-03-16T17:00:00",response.getEndDateTime());
        assertEquals("PT8H",response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(),response.getEventUserDaysResponse());
        assertEquals("2020-03-16",response.getDate());
    }

    @Test
    void shouldCreateFromUserEventWithEventTypeCompany() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.eventType = EventType.COMPANY;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("COMPANY",response.getEventType());
    }

    @Test
    void shouldCreateFromUserEventWithEventTypePersonal() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.eventType = EventType.PERSONAL;
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("PERSONAL",response.getEventType());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeInTwoDifferentDays() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,17,17,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00",response.getStartDateTime());
        assertEquals("2020-03-17T17:00:00",response.getEndDateTime());
        assertEquals("PT32H",response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(),response.getEventUserDaysResponse());
        assertEquals("2020-03-16",response.getDate());
    }

    @Test
    void shouldCreateFromUserEventWithStartDateTimeAndEndDateTimeWithAMiddleDay() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,18,17,0,0);
        var response = UserEventResponse.createFromUserEvent(userEvent);

        assertEquals("2020-03-16T09:00:00",response.getStartDateTime());
        assertEquals("2020-03-18T17:00:00",response.getEndDateTime());
        assertEquals("PT56H",response.getDuration());
        assertEquals(UserEventResponse.createFromUserEvent(userEvent).getEventUserDaysResponse(),response.getEventUserDaysResponse());
        assertEquals("2020-03-16",response.getDate());
    }

    @Test
    void shouldNotCreateFromUserEventWithStartDateTimeBeforeEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,15,17,0,0);
        assertThrows(IllegalArgumentException.class, () -> UserEventResponse.createFromUserEvent(userEvent));
    }

    @Test
    void shouldNotCreateFromUserEventWithSameStartDateTimeAndEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.id = 1L;
        userEvent.name = "Event name";
        userEvent.description = "Event description";
        userEvent.startDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        userEvent.endDateTime = LocalDateTime.of(2020,03,16,9,0,0);
        assertThrows(IllegalArgumentException.class, () -> UserEventResponse.createFromUserEvent(userEvent));
    }

}