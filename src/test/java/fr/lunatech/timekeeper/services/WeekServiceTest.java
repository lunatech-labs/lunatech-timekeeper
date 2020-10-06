package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeekServiceTest {

    WeekService weekService = new WeekService();

    @Test
    void getTimeEntriesDurationOfOneTimeEntryForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                1L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1),
                null,
                null);
        assertEquals(
                1L,
                weekService.getTimeEntriesDurationForASpecificDay(date, user.getId(), List.of(timeSheetResponse)));
    }

    @Test
    void getTimeEntriesDurationOfMultipleTimeEntryForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                1L);
        TimeSheetResponse.TimeEntryResponse timeEntry2 = new TimeSheetResponse.TimeEntryResponse(
                4L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse.TimeEntryResponse timeEntry3 = new TimeSheetResponse.TimeEntryResponse(
                5L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1, timeEntry2, timeEntry3),
                null,
                null);
        assertEquals(
                7L,
                weekService.getTimeEntriesDurationForASpecificDay(date, user.getId(), List.of(timeSheetResponse)));
    }

    @Test
    void getTimeEntriesDurationOfMultipleTimeEntryForASpecificDayForMultipleUsers() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        User user2 = new User(6L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                1L);
        TimeSheetResponse.TimeEntryResponse timeEntry2 = new TimeSheetResponse.TimeEntryResponse(
                4L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse.TimeEntryResponse timeEntry3 = new TimeSheetResponse.TimeEntryResponse(
                5L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1, timeEntry2),
                null,
                null);
        TimeSheetResponse timeSheetResponse2 = new TimeSheetResponse(
                7L,
                projectResponse,
                user2.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry3),
                null,
                null);
        assertEquals(
                4L,
                weekService.getTimeEntriesDurationForASpecificDay(date, user.getId(), List.of(timeSheetResponse, timeSheetResponse2)));
    }

    @Test
    void getTimeEntriesDurationOfMultipleTimeEntryForASpecificDayWithOneOnAnOthenDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                1L);
        TimeSheetResponse.TimeEntryResponse timeEntry2 = new TimeSheetResponse.TimeEntryResponse(
                4L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse.TimeEntryResponse timeEntry3 = new TimeSheetResponse.TimeEntryResponse(
                5L,
                "",
                LocalDateTime.of(2020, 10, 7, 9, 0, 0),
                3L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1, timeEntry2, timeEntry3),
                null,
                null);
        assertEquals(
                4L,
                weekService.getTimeEntriesDurationForASpecificDay(date, user.getId(), List.of(timeSheetResponse)));
    }

    @Test
    void getUserEventsDurationOfOneUserEventOfOneDayForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        final UserEvent userEvent = new UserEvent(
                1L,
                LocalDate.of(2020, 10, 6).atTime(9, 0),
                LocalDate.of(2020, 10, 6).atTime(17, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        assertEquals(8L,
                weekService.getUserEventsDurationForASpecificDay(date, user.getId(), List.of(userEventResponse)));
    }

    @Test
    void getUserEventsDurationOfOneUserEventOfMultipleDaysForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        final UserEvent userEvent = new UserEvent(
                1L,
                LocalDate.of(2020, 10, 6).atTime(15, 0),
                LocalDate.of(2020, 10, 10).atTime(17, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        assertEquals(2L,
                weekService.getUserEventsDurationForASpecificDay(date, user.getId(), List.of(userEventResponse)));
    }

    @Test
    void getUserEventsDurationOfOneUserEventOfMultipleDaysForASpecificDayInTheMiddleOfTheEvent() {
        LocalDate date = LocalDate.of(2020, 10, 7);
        User user = new User(0L, "", "", "", "");
        final UserEvent userEvent = new UserEvent(
                1L,
                LocalDate.of(2020, 10, 6).atTime(15, 0),
                LocalDate.of(2020, 10, 10).atTime(17, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        assertEquals(8L,
                weekService.getUserEventsDurationForASpecificDay(date, user.getId(), List.of(userEventResponse)));
    }

    @Test
    void getUserEventsDurationOfMultipleUserEventsForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        final UserEvent userEvent = new UserEvent(
                1L,
                LocalDate.of(2020, 10, 6).atTime(9, 0),
                LocalDate.of(2020, 10, 6).atTime(11, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        final UserEvent userEvent2 = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(15, 0),
                LocalDate.of(2020, 10, 6).atTime(17, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        UserEventResponse userEventResponse2 = UserEventResponse.bind(userEvent2);
        assertEquals(4L,
                weekService.getUserEventsDurationForASpecificDay(date, user.getId(), List.of(userEventResponse, userEventResponse2)));
    }

    @Test
    void getUserEventsDurationOfMultipleUserEventForMultipleUsersForASpecificDay() {
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        User user2 = new User(1L, "", "", "", "");
        final UserEvent userEvent = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(9, 0),
                LocalDate.of(2020, 10, 6).atTime(11, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        final UserEvent userEvent2 = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(12, 0),
                LocalDate.of(2020, 10, 6).atTime(13, 0),
                "",
                "",
                EventType.PERSONAL,
                user2,
                user2
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        UserEventResponse userEventResponse2 = UserEventResponse.bind(userEvent2);
        assertEquals(2L,
                weekService.getUserEventsDurationForASpecificDay(date, user.getId(), List.of(userEventResponse, userEventResponse2)));
        assertEquals(1L,
                weekService.getUserEventsDurationForASpecificDay(date, user2.getId(), List.of(userEventResponse, userEventResponse2)));
    }

    @Test
    void shouldReturn8HoursOfTimeEntriesAndUserEvent(){
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        final UserEvent userEvent = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(9, 0),
                LocalDate.of(2020, 10, 6).atTime(12, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                5L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1),
                null,
                null);
        assertEquals(8L, weekService.getWorkingHoursForASpecificDay(date,user.getId(),List.of(timeSheetResponse),List.of(userEventResponse)));
    }

    @Test
    void shouldReturn8HoursOfTimeEntriesAndMultipleDaysUserEvent(){
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        final UserEvent userEvent = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(14, 0),
                LocalDate.of(2020, 10, 8).atTime(12, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                5L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1),
                null,
                null);
        assertEquals(8L, weekService.getWorkingHoursForASpecificDay(date,user.getId(),List.of(timeSheetResponse),List.of(userEventResponse)));
    }

    @Test
    void shouldReturn8HoursOfMultipleTimeEntriesForMultipleUsersAndMultipleDaysUserEvent(){
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        User user2 = new User(6L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        final UserEvent userEvent = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(14, 0),
                LocalDate.of(2020, 10, 8).atTime(12, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        final UserEvent userEvent2 = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(14, 0),
                LocalDate.of(2020, 10, 8).atTime(12, 0),
                "",
                "",
                EventType.PERSONAL,
                user2,
                user2
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        UserEventResponse userEventResponse2 = UserEventResponse.bind(userEvent2);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                5L);
        TimeSheetResponse.TimeEntryResponse timeEntry2 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                3L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1),
                null,
                null);
        TimeSheetResponse timeSheetResponse2 = new TimeSheetResponse(
                2L,
                projectResponse,
                user2.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry2),
                null,
                null);
        assertEquals(8L, weekService.getWorkingHoursForASpecificDay(date,user.getId(),List.of(timeSheetResponse, timeSheetResponse2),List.of(userEventResponse, userEventResponse2)));
        assertEquals(6L, weekService.getWorkingHoursForASpecificDay(date,user2.getId(),List.of(timeSheetResponse, timeSheetResponse2),List.of(userEventResponse, userEventResponse2)));
    }

    @Test
    void shouldReturn4HoursOfTimeEntriesAndNoUserEvent(){
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        TimeSheetResponse.TimeEntryResponse timeEntry1 = new TimeSheetResponse.TimeEntryResponse(
                3L,
                "",
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                4L);
        TimeSheetResponse timeSheetResponse = new TimeSheetResponse(
                2L,
                projectResponse,
                user.getId(),
                TimeUnit.HOURLY,
                false,
                null,
                null,
                null,
                List.of(timeEntry1),
                null,
                null);
        assertEquals(4L, weekService.getWorkingHoursForASpecificDay(date,user.getId(),List.of(timeSheetResponse), Collections.emptyList()));
    }

    @Test
    void shouldReturn4HoursOfUserEventAndNoTimeEntry(){
        LocalDate date = LocalDate.of(2020, 10, 6);
        User user = new User(0L, "", "", "", "");
        ProjectResponse projectResponse = new ProjectResponse(1L, "name", false, "description", null, null, true, 0L);
        final UserEvent userEvent = new UserEvent(
                2L,
                LocalDate.of(2020, 10, 6).atTime(9, 0),
                LocalDate.of(2020, 10, 6).atTime(13, 0),
                "",
                "",
                EventType.PERSONAL,
                user,
                user
        );
        UserEventResponse userEventResponse = UserEventResponse.bind(userEvent);
        assertEquals(4L, weekService.getWorkingHoursForASpecificDay(date,user.getId(),Collections.emptyList(),List.of(userEventResponse)));
    }
}