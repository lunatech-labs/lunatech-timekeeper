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

package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.PublicHoliday;
import fr.lunatech.timekeeper.timeutils.Week;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MonthResponse {

    private final List<UserEventResponse> userEvents;

    private final List<TimeSheetResponse> sheets;

    private final List<PublicHoliday> publicHolidays;

    public MonthResponse(
            @NotNull List<UserEventResponse> userEvents,
            @NotNull List<TimeSheetResponse> sheets,
            @NotNull List<PublicHoliday> publicHolidays
    ) {
        this.userEvents = userEvents;
        this.sheets = sheets;
        this.publicHolidays = publicHolidays;
    }

    public static MonthResponse bind(@NotNull Week week,
                                     List<TimeSheet> sheets,
                                     List<PublicHoliday> publicHolidays) {
        return new MonthResponse(
                week.userEvents.stream()
                        .map(UserEventResponse::bind)
                        .collect(Collectors.toList()),
                sheets.stream()
                        .map(TimeSheetResponse::bind)
                        .collect(Collectors.toList()),
                publicHolidays
        );
    }

    public List<UserEventResponse> getUserEvents() {
        return Collections.unmodifiableList(userEvents);
    }

    public List<TimeSheetResponse> getSheets() {
        return Collections.unmodifiableList(sheets);
    }

    public List<PublicHoliday> getPublicHolidays() {
        return Collections.unmodifiableList(publicHolidays);
    }
}
