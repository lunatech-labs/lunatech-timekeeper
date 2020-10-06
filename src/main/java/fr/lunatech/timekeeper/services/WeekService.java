/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeekService {

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    UserEventService userEventService;

    /**
     * Loads a specific week for the current authenticated user
     *
     * @param ctx        is the context with the user that made this request
     * @param year       is a 4 digit Integer value for a year
     * @param weekNumber is an Integer value between 1 and 52
     * @return the WeekResponse with details for this user.
     */
    public WeekResponse getWeek(AuthenticationContext ctx, Integer year, Integer weekNumber) {
        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current week");
        }

        var userEvents = userEventService.getEventsByUserForWeekNumber(maybeUser.get().id, weekNumber, year);
        var publicHolidays = CalendarFactory.instanceFor("FR", year).getPublicHolidaysForWeekNumber(weekNumber);

        var startDayOfWeek = TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(year, weekNumber);
        final List<TimeSheetResponse> timeSheetsResponse = new ArrayList<>();
        for (TimeSheetResponse timeSheetResponse : timeSheetService.findAllActivesForUser(ctx)) {
            timeSheetsResponse.add(timeSheetResponse.filterTimeEntriesForWeek(startDayOfWeek));
        }

        return new WeekResponse(TimeKeeperDateUtils.adjustToFirstDayOfWeek(startDayOfWeek)
                , userEvents
                , timeSheetsResponse
                , publicHolidays);
    }

    /**
     * Returns the duration of timeEntries in hours for a Specific Day
     *
     * @param date is a LocalDate
     * @param userId is a Long that helps to filter
     * @param sheets is a list of timesheets that contains timeentries
     * @return a Long that is the duration of TimeEntries in hours.
     */
    public Long getTimeEntriesDurationForASpecificDay(LocalDate date, Long userId, List<TimeSheetResponse> sheets){
        return sheets.stream().map(timeSheetResponse -> {
            if(timeSheetResponse.ownerId.equals(userId)){
                return timeSheetResponse.entries.stream().map(timeEntryResponse -> {
                    if(date.equals(timeEntryResponse.getStartDateTime().toLocalDate())){
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
     * @param date is a LocalDate
     * @param userId is a Long that helps to filter
     * @param userEvents is a list of userEvents
     * @return a Long that is the duration of UserEvents in hours.
     */
    public Long getUserEventsDurationForASpecificDay(LocalDate date, Long userId, List<UserEventResponse> userEvents){
        return userEvents.stream().map(userEvent -> {
            if(userEvent.getAttendees().stream().anyMatch(attendee -> attendee.getUserId().equals(userId))){
                return userEvent.getEventUserDaysResponse()
                        .stream()
                        .map(userEventDay -> {
                            if(TimeKeeperDateUtils.formatToLocalDate(userEventDay.getDate()).equals(date)){
                                return TimeKeeperDateUtils.getDuration(TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getStartDateTime()),
                                        TimeKeeperDateUtils.formatToLocalDateTime(userEventDay.getEndDateTime()), ChronoUnit.HOURS);
                            }
                            return 0L;
                        }).reduce(0L, Long::sum);
            }
            return 0L;
        }).reduce(0L, Long::sum);
    }

    /**
     * Returns the duration of working hours for a Specific Day
     *
     * @param date is a LocalDate
     * @param userId is a Long that helps to filter
     * @param userEvents is a list of userEvent
     * @param sheets is a list of timesheet
     * @return a Long that is the duration of UserEvents in hours.
     */
    public Long getWorkingHoursForASpecificDay(
            LocalDate date,
            Long userId,
            List<TimeSheetResponse> sheets,
            List<UserEventResponse> userEvents){
        return getTimeEntriesDurationForASpecificDay(date, userId, sheets)
                + getUserEventsDurationForASpecificDay(date, userId, userEvents);
    }
}
