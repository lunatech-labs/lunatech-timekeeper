package fr.lunatech.timekeeper.services.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import fr.lunatech.timekeeper.timeutils.TimeSheetUtils;
import fr.lunatech.timekeeper.timeutils.TimeUnit;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TimeSheetResponse {
    public final Long id;

    public final ProjectResponse project;

    public final Long ownerId;

    public final TimeUnit timeUnit;

    public final Boolean defaultIsBillable;

    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_PATTERN)
    public final LocalDate expirationDate;

    public final Integer maxDuration; // eg 21

    public final String durationUnit; // DAYS

    public final List<TimeEntryResponse> entries;

    public final Long leftOver;

    public TimeSheetResponse(Long id, ProjectResponse project, Long ownerId, TimeUnit timeUnit,
                             Boolean defaultIsBillable, LocalDate expirationDate, Integer maxDuration, String durationUnit, List<TimeEntryResponse> entries, Long leftOver) {
        this.id = id;
        this.project = project;
        this.ownerId = ownerId;
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
        this.leftOver = leftOver;
    }

    public static TimeSheetResponse bind(@NotNull TimeSheet sheet) {
        return new TimeSheetResponse(
                sheet.id,
                ProjectResponse.bind(sheet.project),
                sheet.owner.id,
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
        public final Long id;

        @NotNull
        public final String comment;

        @NotNull
        @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
        public final LocalDateTime startDateTime;

        @NotNull
        @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
        public final LocalDateTime endDateTime;

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

        @Override
        public String toString() {
            return "TimeEntryResponse[id=" + id + "]" ;
        }
    }

    public final TimeSheetResponse filterToSixWeeksRange(Integer year, Integer monthNumber) {
        Predicate<LocalDate> isValidDate = TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(year, monthNumber);
        List<TimeEntryResponse> restrictedEntries = this.entries
                .stream()
                .filter(timeEntryResponse -> {
                    LocalDateTime startDateTime = timeEntryResponse.startDateTime;
                    return isValidDate.test(startDateTime.toLocalDate());
                })
                .collect(Collectors.toList());

        return new TimeSheetResponse(this.id,
                this.project,
                this.ownerId,
                this.timeUnit,
                this.defaultIsBillable,
                this.expirationDate,
                this.maxDuration,
                this.durationUnit,
                restrictedEntries,
                this.leftOver);
    }

    public final TimeSheetResponse filterTimeEntriesForWeek(LocalDate startDayOfWeek){
        List<TimeEntryResponse> restrictedEntries = this.entries
                .stream()
                .filter(timeEntryResponse -> TimeKeeperDateUtils.isSameWeekAndYear(timeEntryResponse.startDateTime.toLocalDate(), startDayOfWeek))
                .collect(Collectors.toList());

        return new TimeSheetResponse(this.id,
                this.project,
                this.ownerId,
                this.timeUnit,
                this.defaultIsBillable,
                this.expirationDate,
                this.maxDuration,
                this.durationUnit,
                restrictedEntries,
                this.leftOver);
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
