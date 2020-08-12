package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEventResponseTest {

    @Test
    void shouldFormatUserEventResponseWithNoStartDateTime() {
        var userEvent = new UserEvent();
        userEvent.startDateTime = null;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertNull(result.getStartDateTime());
    }

    @Test
    void shouldFormatUserEventResponseWithStartDateTime() {
        var userEvent = new UserEvent();
        var february = LocalDateTime.of(2020,02,29,12,23,13);
        userEvent.startDateTime = february;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals( "2020-02-29T12:23:13", result.getStartDateTime());
        assertNull(result.getEndDateTime());
    }

    @Test
    void shouldFormatUserEventResponseWithNoDate() {
        var userEvent = new UserEvent();
        userEvent.startDateTime = null;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertNull(result.getDate());
    }

    @Test
    void shouldFormatUserEventResponseWithDate() {
        var userEvent = new UserEvent();
        var february = LocalDateTime.of(2020,02,29,12,23,13);
        userEvent.startDateTime = february;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals( "2020-02-29", result.getDate());
    }

    @Test
    void shouldFormatUserEventResponseWithNoEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.endDateTime = null;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertNull(result.getEndDateTime());
    }

    @Test
    void shouldFormatUserEventResponseWithEndDateTime() {
        var userEvent = new UserEvent();
        var february = LocalDateTime.of(2020,02,29,12,23,13);
        userEvent.endDateTime = february;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals( "2020-02-29T12:23:13", result.getEndDateTime());
    }

    @Test
    void shouldFormatUserEventResponseWithNoEventType() {
        var userEvent = new UserEvent();
        userEvent.eventType = null;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertNull(result.getEventType());
    }

    @Test
    void shouldFormatUserEventResponseWithEventTypeCompany() {
        var userEvent = new UserEvent();
        userEvent.eventType = EventType.COMPANY;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals(EventType.COMPANY.toString(),result.getEventType());
    }

    @Test
    void shouldFormatUserEventResponseWithEventTypePersonal() {
        var userEvent = new UserEvent();
        userEvent.eventType = EventType.PERSONAL;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals(EventType.PERSONAL.toString(),result.getEventType());
    }

    @Test
    void shouldFormatUserEventResponseWithNoStartDateTimeAndNoEndDateTime() {
        var userEvent = new UserEvent();
        userEvent.startDateTime = null;
        userEvent.endDateTime = null;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals("",result.getDuration());
    }

    @Test
    void shouldFormatUserEventResponseWithStartDateTimeAndEndDateTime() {
        var userEvent = new UserEvent();
        var februaryStart = LocalDateTime.of(2020,02,29,12,23,13);
        var marchEnd = LocalDateTime.of(2020,03,01,15,22,0);
        userEvent.startDateTime = februaryStart;
        userEvent.endDateTime = marchEnd;
        var result = UserEventResponse.formatUserEventResponse(userEvent,
                UserEventResponse.createFromUserEvent(userEvent)
        );

        assertEquals("PT26H58M47S",result.getDuration());
    }



    @Test
    void ShouldCreateEventUserDayResponseWithStartDateTimeAndEndDateTimeInSameDay() {
        var userEvent = new UserEvent();
        var februaryStart = LocalDateTime.of(2020,02,29,12,0,0);
        var februaryEnd = LocalDateTime.of(2020,02,29,15,0,0);
        userEvent.startDateTime = februaryStart;
        userEvent.endDateTime = februaryEnd;
        var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
        var result = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);

        var expected = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        assertEquals(expected, result);
    }

    @Test
    void ShouldCreateEventUserDayResponseWithStartDateTimeAndEndDateTimeInTwoDifferentDays() {
        var userEvent = new UserEvent();
        var februaryStart = LocalDateTime.of(2020,02,29,12,0,0);
        var marchEnd = LocalDateTime.of(2020,03,01,15,0,0);
        userEvent.startDateTime = februaryStart;
        userEvent.endDateTime = marchEnd;
        var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
        var result = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);

        var expected = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        assertEquals(expected, result);
    }

    @Test
    void ShouldCreateEventUserDayResponseWithStartDateTimeAndEndDateTimeInThreeDifferentDays() {
        var userEvent = new UserEvent();
        var februaryStart = LocalDateTime.of(2020,02,29,12,0,0);
        var marchEnd = LocalDateTime.of(2020,03,02,15,0,0);
        userEvent.startDateTime = februaryStart;
        userEvent.endDateTime = marchEnd;
        var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
        var result = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);

        var expected = UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        assertEquals(expected, result);
    }

    @Test
    void ShouldNotCreateEventUserDayResponseWithNoStartDateTime() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            var userEvent = new UserEvent();
            var marchEnd = LocalDateTime.of(2020,03,02,15,0,0);
            userEvent.startDateTime = null;
            userEvent.endDateTime = marchEnd;
            var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
            UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        });
    }

    @Test
    void ShouldNotCreateEventUserDayResponseWithNoEndDateTime() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            var userEvent = new UserEvent();
            var marchStart = LocalDateTime.of(2020,03,02,15,0,0);
            userEvent.startDateTime = marchStart;
            userEvent.endDateTime = null;
            var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
            UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        });
    }

    @Test
    void ShouldNotCreateEventUserDayResponseWithNoStartDateTimeAndNoEndDateTime() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            var userEvent = new UserEvent();
            userEvent.startDateTime = null;
            userEvent.endDateTime = null;
            var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
            UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        });
    }

    @Test
    void ShouldNotEventUserDayResponseWithEndDateTimeBeforeStartDateTime() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var userEvent = new UserEvent();
            var februaryEnd = LocalDateTime.of(2020, 02, 29, 12, 0, 0);
            var marchStart = LocalDateTime.of(2020, 03, 02, 15, 0, 0);
            userEvent.startDateTime = marchStart;
            userEvent.endDateTime = februaryEnd;
            var userEventResponse = UserEventResponse.createFromUserEvent(userEvent);
            UserEventResponse.createEventUserDayResponse(userEvent, userEventResponse);
        });
    }

    @Test
    void ShouldBindWithNoStartDateTime() {
        var userEvent = new UserEvent();
        var marchEnd = LocalDateTime.of(2020,03,02,15,0,0);
        userEvent.startDateTime = null;
        userEvent.endDateTime = marchEnd;
        var result = UserEventResponse.bind(userEvent);
        var expected = UserEventResponse.formatUserEventResponse(userEvent, UserEventResponse.createFromUserEvent(userEvent));

        assertEquals(expected, result);
    }

    @Test
    void ShouldBindWithNoEndDateTime() {
        var userEvent = new UserEvent();
        var marchStart = LocalDateTime.of(2020,03,02,15,0,0);
        userEvent.startDateTime = marchStart;
        userEvent.endDateTime = null;
        var result = UserEventResponse.bind(userEvent);
        var expected = UserEventResponse.formatUserEventResponse(userEvent, UserEventResponse.createFromUserEvent(userEvent));

        assertEquals(expected, result);
    }

    @Test
    void ShouldBindWithStartDateTimeAndEndDateTime() {
        var userEvent = new UserEvent();
        var marchStart = LocalDateTime.of(2020,03,01,15,0,0);
        var marchEnd = LocalDateTime.of(2020,03,02,16,0,0);
        userEvent.startDateTime = marchStart;
        userEvent.endDateTime = marchEnd;
        var result = UserEventResponse.bind(userEvent);
        var expected = UserEventResponse.formatUserEventResponse(userEvent, UserEventResponse.createEventUserDayResponse(userEvent, UserEventResponse.createFromUserEvent(userEvent)));

        assertEquals(expected, result);
    }
}