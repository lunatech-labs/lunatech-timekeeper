package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class WeekUtils {

    private WeekUtils() {}

    /**
     * Returns the duration of working hours for a Specific Day
     *
     * @param date       is a LocalDate
     * @param userId     is a Long that helps to filter
     * @param userEvents is a list of userEvent
     * @param sheets     is a list of timesheet
     * @return a Long that is the duration of UserEvents in hours.
     */
    public static Long getWorkingHoursForASpecificDay(
            LocalDate date,
            Long userId,
            List<TimeSheetResponse> sheets,
            List<UserEventResponse> userEvents) {
        return getTimeEntriesDurationForASpecificDay(date, userId, sheets)
                + getUserEventsDurationForASpecificDay(date, userId, userEvents);
    }

    /**
     * Returns the duration of timeEntries in hours for a Specific Day
     *
     * @param date   is a LocalDate
     * @param userId is a Long that helps to filter
     * @param sheets is a list of timesheets that contains timeentries
     * @return a Long that is the duration of TimeEntries in hours.
     */
    public static Long getTimeEntriesDurationForASpecificDay(LocalDate date, Long userId, List<TimeSheetResponse> sheets) {
        return sheets.stream()
                .filter(timeSheetResponse -> timeSheetResponse.ownerId.equals(userId))
                .flatMap(timeSheetResponse -> timeSheetResponse.entries.stream())
                .filter(i -> date.equals(i.getStartDateTime().toLocalDate()))
                .map(i -> i.numberOfHours)
                .reduce(0L, Long::sum);
    }

    /**
     * Returns the duration of userEvents in hours for a Specific Day
     *
     * @param date       is a LocalDate
     * @param userId     is a Long that helps to filter
     * @param userEvents is a list of userEvents
     * @return a Long that is the duration of UserEvents in hours.
     */
    public static Long getUserEventsDurationForASpecificDay(LocalDate date, Long userId, List<UserEventResponse> userEvents) {
        return userEvents.stream()
                .filter(userEvent -> userEvent.getAttendees().stream().anyMatch(attendee -> attendee.getUserId().equals(userId)))
                .flatMap(userEvent -> userEvent.getEventUserDaysResponse().stream())
                .filter(day -> TimeKeeperDateUtils.formatToLocalDate(day.getDate()).equals(date))
                .map(userEventDay -> TimeKeeperDateUtils.getDuration(TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getStartDateTime()),
                        TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getEndDateTime()), ChronoUnit.HOURS))
                .reduce(0L, Long::sum);
    }
}
