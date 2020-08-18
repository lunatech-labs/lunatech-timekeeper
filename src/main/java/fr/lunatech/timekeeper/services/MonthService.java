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
import fr.lunatech.timekeeper.services.responses.MonthResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MonthService {

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    UserEventService userEventService;

    public MonthResponse getMonth(AuthenticationContext ctx, Integer year, Integer monthNumber) {
        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current month");
        }

        var userEvents = userEventService.getEventsByUserForMonthNumber(maybeUser.get().id, monthNumber);
        if (monthNumber == null) {
            monthNumber = LocalDate.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        var publicHolidays = CalendarFactory.instanceFor("FR", year).getPublicHolidaysForMonthNumber(monthNumber);

        final List<TimeSheetResponse> timeSheetResponseList = new ArrayList<>();
        for (TimeSheetResponse timeSheetResponse : timeSheetService.findAllActivesForUser(ctx)) {
            timeSheetResponseList.add(timeSheetResponse.filterToSixWeeksRange(year, monthNumber));
        }

        return new MonthResponse(userEvents
                , timeSheetResponseList
                , publicHolidays);

    }
}
