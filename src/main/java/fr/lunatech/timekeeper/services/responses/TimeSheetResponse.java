package fr.lunatech.timekeeper.services.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
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

    public LocalDate expirationDate;

    public Integer maxDuration; // eg 21

    public String durationUnit; // DAYS

    public List<TimeEntryResponse> entries;

    public TimeSheetResponse(Long id, ProjectResponse project, UserResponse owner, TimeUnit timeUnit, Boolean defaultIsBillable, LocalDate expirationDate, Integer maxDuration, String durationUnit, List<TimeEntryResponse> entries) {
        this.id = id;
        this.project = project;
        this.ownerId = owner.getId();
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
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
                        .collect(Collectors.toList())
        );
    }

    public static final class TimeEntryResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean billable;

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
                @NotNull Boolean billable,
                @NotNull String comment,
                @NotNull LocalDateTime startDateTime,
                @NotNull LocalDateTime endDateTime
        ) {
            this.id = id;
            this.billable = billable;
            this.comment = comment;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public static TimeSheetResponse.TimeEntryResponse bind(@NotNull TimeEntry timeEntry) {
            return new TimeSheetResponse.TimeEntryResponse(
                    timeEntry.id,
                    timeEntry.billable,
                    timeEntry.comment,
                    timeEntry.startDateTime,
                    timeEntry.endDateTime
            );
        }

        public Long getId() {
            return id;
        }

        public Boolean getBillable() {
            return billable;
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
                '}';
    }
}
