package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class WeekUtils {
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
        return sheets.stream().map(timeSheetResponse -> {
            if (timeSheetResponse.ownerId.equals(userId)) {
                return timeSheetResponse.entries.stream().map(timeEntryResponse -> {
                    if (date.equals(timeEntryResponse.getStartDateTime().toLocalDate())) {
                        return timeEntryResponse.numberOfHours;
                    }
                    return 0L;
                }).reduce(0L, Long::sum);
            }
            return 0L;
        }).reduce(0L, Long::sum);
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
        return userEvents.stream().map(userEvent -> {
            if (userEvent.getAttendees().stream().anyMatch(attendee -> attendee.getUserId().equals(userId))) {
                return userEvent.getEventUserDaysResponse()
                        .stream()
                        .map(userEventDay -> {
                            if (TimeKeeperDateUtils.formatToLocalDate(userEventDay.getDate()).equals(date)) {
                                return TimeKeeperDateUtils.getDuration(TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getStartDateTime()),
                                        TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getEndDateTime()), ChronoUnit.HOURS);
                            }
                            return 0L;
                        }).reduce(0L, Long::sum);
            }
            return 0L;
        }).reduce(0L, Long::sum);
    }
}
