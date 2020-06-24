/*
 * Copyright 2020 Lunatech Labs
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

package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TimeEntryPerHourRequest implements TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final LocalDateTime startDateTime;

    @NotNull
    private final LocalDateTime endDateTime;

    public TimeEntryPerHourRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull LocalDateTime startDateTime,
            @NotNull LocalDateTime endDateTime
    ) {
        this.comment = comment;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = this.startDateTime;
        timeEntry.endDateTime = this.endDateTime;
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalEntityStateException("Start dateTime cannot be after endDateTime, invalid entry");
        }
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId, ctx).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return "TimeEntryRequest{" +
                "comment='" + comment + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                '}';
    }
}
