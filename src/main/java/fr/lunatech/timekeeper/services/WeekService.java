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
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
        Optional<User> maybeUser = userService.findUserById(userId, ctx);
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


}
