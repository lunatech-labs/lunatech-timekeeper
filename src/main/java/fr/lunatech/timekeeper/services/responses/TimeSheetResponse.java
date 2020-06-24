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

package fr.lunatech.timekeeper.services.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.TimeSheetUtils;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;
import fr.lunatech.timekeeper.timeutils.TimeUnit;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSheetResponse {
    public Long id;

    public ProjectResponse project;

    public Long ownerId;

    public TimeUnit timeUnit;

    public Boolean defaultIsBillable;

    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_PATTERN)
    public LocalDate expirationDate;

    public Integer maxDuration; // eg 21

    public String durationUnit; // DAYS

    public List<TimeEntryResponse> entries;

    public Long leftOver;

    public TimeSheetResponse(Long id, ProjectResponse project, UserResponse owner, TimeUnit timeUnit, Boolean defaultIsBillable, LocalDate expirationDate, Integer maxDuration, String durationUnit, List<TimeEntryResponse> entries, Long leftOver) {
        this.id = id;
        this.project = project;
        this.ownerId = owner.getId();
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
        this.leftOver=leftOver;
    }

    public static TimeSheetResponse bind(@NotNull TimeSheet sheet) {
        return new TimeSheetResponse(
                sheet.id,
                ProjectResponse.bind(sheet.project),
                UserResponse.bind(sheet.owner),
                sheet.timeUnit,
                sheet.defaultIsBillable,
                sheet.expirationDate,
                sheet.maxDuration,
                sheet.durationUnit.name(),
                sheet.entries.stream().map(TimeSheetResponse.TimeEntryResponse::bind)
                        .collect(Collectors.toList()),
                TimeSheetUtils.computeLeftOver(sheet)
        );
    }

    public static final class TimeEntryResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final String comment;

        @NotNull
        @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
        private final LocalDateTime startDateTime;

        @NotNull
        @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
        private final LocalDateTime endDateTime;

        public TimeEntryResponse(
                @NotNull Long id,
                @NotNull String comment,
                @NotNull LocalDateTime startDateTime,
                @NotNull LocalDateTime endDateTime
        ) {
            this.id = id;
            this.comment = comment;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public static TimeSheetResponse.TimeEntryResponse bind(@NotNull TimeEntry timeEntry) {
            return new TimeSheetResponse.TimeEntryResponse(
                    timeEntry.id,
                    timeEntry.comment,
                    timeEntry.startDateTime,
                    timeEntry.endDateTime
            );
        }

        public Long getId() {
            return id;
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
    }

    @Override
    public String toString() {
        return "TimeSheetResponse{" +
                "id=" + id +
                ", project=" + project +
                ", ownerId=" + ownerId +
                ", timeUnit=" + timeUnit +
                ", defaultIsBillable=" + defaultIsBillable +
                ", expirationDate=" + expirationDate +
                ", maxDuration=" + maxDuration +
                ", durationUnit='" + durationUnit + '\'' +
                ", entries=" + entries +
                ", leftOver=" + leftOver +
                '}';
    }
}
